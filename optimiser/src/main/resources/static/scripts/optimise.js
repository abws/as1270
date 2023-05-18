const form = document.getElementById("form")
const monitor = document.getElementById("monitor")
const update = document.getElementById("update")
const energy = document.getElementById("energy")
const stop = document.getElementById("stop")
const complete = document.getElementById("complete")
const algorithms = document.querySelectorAll(".algorithm")
algorithms.forEach((algorithm) => {
    algorithm.addEventListener("click", () => {

        // Remove active class from all algorithms
        algorithms.forEach((otherAlgorithm) => {
            if (otherAlgorithm !== algorithm) {
                otherAlgorithm.classList.remove("active");
                otherAlgorithm.querySelector('.algo').value = 0;
                let otherInputs = otherAlgorithm.querySelectorAll('.algorithm-input');
                otherInputs.forEach((input) => {
                    input.classList.add("destroy");
                });
            }
        });

        // Add active class to the clicked algorithm
        algorithm.classList.add("active")
        algorithm.querySelector('.algo').value = 1;
        let inputs = algorithm.querySelectorAll('.algorithm-input');
        inputs.forEach((input) => {
            input.classList.remove("destroy");
        });
    });
});



const body = document.body
let i = 0
let max
let plotData = [];
localStorage.setItem('coordinates', JSON.stringify([]))

const margin = { top: 20, right: 20, bottom: 30, left: 50 };
const width = 500 - margin.left - margin.right;
const height = 300 - margin.top - margin.bottom;

const x = d3.scaleLinear().range([0, width]);
const y = d3.scaleLinear().range([height, 0]);

const line = d3.line()
    .x(d => x(d.generation))
    .y(d => y(d.fitness));

const svg = d3.select('#chart').append('svg')
    .attr('width', width + margin.left + margin.right)
    .attr('height', height + margin.top + margin.bottom)
    .append('g')
    .attr('transform', 'translate(' + margin.left + ',' + margin.top + ')');



form.addEventListener("submit", (event) => {
    event.preventDefault();
    body.classList.toggle("blur")
    monitor.classList.toggle("hide")


    const data = new FormData(form);

    const jsonData = {};
    for (const [key, value] of data.entries()) {
        if(value instanceof File){
            // Create a new FileReader object
            let reader = new FileReader();

            // Add an event listener for when the file has been loaded
            reader.addEventListener("load", function () {
                let base64String = this.result.split(',')[1];
                jsonData[key] = base64String;
            }, false);
            reader.readAsDataURL(value);
        }
        else{
            jsonData[key] = value;
        }
    }
    // Connect to the WebSocket
    const socket = new WebSocket("ws://localhost:8080/optimise-socket");
    socket.onopen = (event) => {
        socket.send(JSON.stringify(jsonData));
        updateChart(plotData);
    };

    socket.onmessage = (event) => {
        if (event.data[0] == "[") {
            localStorage.setItem('coordinates', event.data)
            stop.classList.toggle("destroy")
            complete.classList.toggle("destroy")
            update.innerHTML = "optimisation complete (100%)"
            return
        }
        plotData.push({ generation: i++, fitness: parseFloat(event.data) });
        updateChart(plotData);
        update.innerHTML = "optimising...(" + Math.round(((i) / jsonData["generations"])*100)+"%)"
        max = Math.max(...plotData.map(row => row.fitness))
        energy.innerHTML = "best: " + max;
    };

    socket.onerror = (error) => {
        console.log(`WebSocket error: ${error}`);
    };

    socket.onclose = (event) => {
        console.log("WebSocket connection closed");
    };
});

function updateChart(plotData) {
    x.domain(d3.extent(plotData, d => d.generation));
    y.domain(d3.extent(plotData, d => d.fitness));

    svg.selectAll('path')
        .data([plotData])
        .join('path')
        .attr('d', line)
        .attr('fill', 'none')
        .attr('stroke', 'steelblue')
        .attr('stroke-width', '1');

    svg.selectAll('g.axis').remove();

    svg.append('g')
        .attr('class', 'axis')
        .attr('transform', 'translate(0,' + height + ')')
        .attr('stroke-width', '0.5')
        .call(d3.axisBottom(x).ticks(5));

    svg.append('g')
        .attr('class', 'axis')
        .attr('stroke-width', '0.5')
        .call(d3.axisLeft(y).ticks(5));
}


complete.addEventListener("click", showVisualise)

function showVisualise() {
    const visualise = document.createElement("div")
    visualise.classList.add("main-visualise")
    visualise.innerHTML=
        `    <div>
                <div class="visualise">
                    <img src="../images/wind-farm.jpg" alt="wind-farm">
                    <div class="redirect">
                        <p>Visualise in 3D</p>
                        <a href="/wind-farm" target="_blank">
                            <svg width="20" height="20" viewBox="0 0 30 30" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <path d="M29.144 0H20.9558C20.4801 0 20.0996 0.38055 20.0996 0.856237C20.0996 1.33192 20.4801 1.71247 20.9558 1.71247H27.0763L13.8776 14.9112C13.5415 15.2474 13.5415 15.7865 13.8776 16.1226C14.0425 16.2875 14.2645 16.3763 14.4801 16.3763C14.6958 16.3763 14.9178 16.2939 15.0827 16.1226L28.2878 2.92389V9.0444C28.2878 9.52008 28.6683 9.90063 29.144 9.90063C29.6197 9.90063 30.0002 9.52008 30.0002 9.0444V0.856237C30.0002 0.38055 29.6197 0 29.144 0Z" fill="#656565"/>
                                <path d="M29.1438 14.6638C28.6681 14.6638 28.2875 15.0444 28.2875 15.5201V23.8224C28.2875 26.2833 26.2833 28.2875 23.8224 28.2875H6.17759C3.7167 28.2875 1.71247 26.2833 1.71247 23.8224V6.17759C1.71247 3.7167 3.7167 1.71247 6.17759 1.71247H14.4165C14.8922 1.71247 15.2727 1.33192 15.2727 0.856237C15.2727 0.38055 14.8922 0 14.4165 0H6.17759C2.77167 0 0 2.77167 0 6.17759V23.8224C0 27.2283 2.77167 30 6.17759 30H23.8224C27.2283 30 30 27.2283 30 23.8224V15.5201C30 15.0444 29.6194 14.6638 29.1438 14.6638Z" fill="#656565"/>
                            </svg>
                        </a>
                    </div>
                </div>
                <div>
                    <span class="result"></span><br>
                    <span class="result">Economical: 0.56kJ/h</span><br>
                    <span class="result">Energy: 789999.87kJ</span>
        
                    <div class="report">
                        <span>Report</span>
                        <svg width="30" height="30" viewBox="0 0 30 30" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <g clip-path="url(#clip0_406_72)">
                                <path d="M14.4125 23.4247C14.5733 23.5856 14.7898 23.6722 15.0001 23.6722C15.2104 23.6722 15.4269 23.5918 15.5877 23.4247L22.2372 16.7753C22.565 16.4474 22.565 15.9216 22.2372 15.5938C21.9094 15.266 21.3836 15.266 21.0557 15.5938L15.8351 20.8144V0.835051C15.8351 0.371134 15.464 0 15.0001 0C14.5362 0 14.165 0.371134 14.165 0.835051V20.8144L8.94441 15.5938C8.61657 15.266 8.0908 15.266 7.76297 15.5938C7.43513 15.9216 7.43513 16.4474 7.76297 16.7753L14.4125 23.4247Z" fill="#757575"/>
                                <path d="M26.3816 28.3299H3.61874C3.15483 28.3299 2.78369 28.701 2.78369 29.1649C2.78369 29.6289 3.15483 30 3.61874 30H26.3816C26.8455 30 27.2167 29.6289 27.2167 29.1649C27.2167 28.701 26.8455 28.3299 26.3816 28.3299Z" fill="#757575"/>
                            </g>
                            <defs>
                                <clipPath id="clip0_406_72">
                                    <rect width="30" height="30" fill="white"/>
                                </clipPath>
                            </defs>
                        </svg>
                    </div>
                </div>
            </div>
            
            <div class="footer">
                <span>Copyright &#169; 2023 Abdiwahab Salah</span>
                <svg width="78" height="46" viewBox="0 0 78 46" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M2.32973 43.329C2.29886 43.6376 1.67988 44.844 2.22096 44.9424C3.51933 45.1784 5.27935 44.6265 6.49925 44.4167C11.9366 43.4814 17.3535 42.465 22.706 41.1173C38.9304 37.032 55.3374 30.8834 72.3051 31.0017C72.3256 31.0018 78.2634 31.2097 76.4021 31.7631C74.9057 32.208 73.3489 32.5083 71.8338 32.887" stroke="#FFB800" stroke-linecap="round"/>
                    <path d="M9.78651 25.3375C9.85877 24.5787 10.105 23.8555 10.4702 23.1838C12.3361 19.7519 15.0986 16.5873 17.5979 13.612C19.8554 10.9244 22.3551 8.4562 24.7597 5.90316C27.5593 2.93077 25.5001 5.19876 24.2811 6.67233C17.1104 15.3402 11.0409 25.1929 7.0004 35.7299C6.57337 36.8435 6.18864 37.897 5.90647 39.0458C5.83307 39.3447 5.61414 39.8312 5.88938 39.9689C6.62903 40.3387 9.92018 37.2995 10.3335 36.9605C12.4633 35.2139 14.6008 33.3669 16.4868 31.3541C17.1757 30.619 18.9254 28.1464 18.3328 28.9612C17.1749 30.5534 16.0496 32.0645 15.4613 33.9864C15.1895 34.8741 16.3616 33.5295 16.3843 33.5078C17.8006 32.1497 19.3051 30.9171 20.8797 29.7474C22.7182 28.3817 20.3156 31.7561 20.3156 32.4823C20.3156 32.8465 23.0792 30.6886 23.2555 30.7046C23.3111 30.7097 22.8669 33.2656 22.9308 33.2514C23.5722 33.1089 24.4273 32.0283 24.8793 31.6276C26.6701 30.0404 28.1601 28.2125 29.7507 26.4315C32.7885 23.0301 36.0232 19.8303 38.8953 16.2784C41.9153 12.5438 46.3402 7.92754 47.5955 3.15124C47.9991 1.61575 47.3614 0.778945 45.8179 1.40778C41.5655 3.14022 38.481 6.70566 35.6648 10.1763C31.6364 15.1409 27.3538 20.983 27.4603 27.6963C27.4919 29.6883 28.5037 32.3171 30.913 31.4738C33.6342 30.5214 36.4564 28.7636 38.7073 26.9784C39.0077 26.7401 41.2532 24.1894 41.237 24.1923C39.171 24.5679 36.5349 28.4487 35.8357 30.2944C35.3208 31.6538 39.0441 28.8639 39.5619 28.4655C41.0935 27.2874 40.3898 28.9751 40.314 30.0551C40.2335 31.203 41.3786 30.3536 41.8182 30.0209C44.4142 28.0564 46.7188 25.5385 48.9629 23.2009C52.2827 19.7428 55.5248 16.2005 58.6545 12.5693C61.2632 9.5425 64.7485 6.10783 66.124 2.22823C66.6237 0.818824 65.8372 0.735903 64.7224 1.40778C60.0177 4.24302 56.5581 8.81314 53.4925 13.253C50.4224 17.6993 46.4977 23.6247 46.7067 29.3714C46.807 32.129 55.4283 26.7804 56.176 26.2605C57.8983 25.0632 60.0556 23.663 61.338 21.9532C62.1156 20.9164 60.7687 21.4969 60.3979 21.8677C59.4563 22.8093 58.1351 24.8133 60.4834 24.2094C62.2194 23.763 63.8674 23.0424 65.4744 22.2608C66.5498 21.7379 69.5705 20.8393 70.1749 19.7311C70.2816 19.5355 69.7294 19.6515 69.5083 19.6799C67.9261 19.8827 66.3964 20.0091 64.7907 20.0388C53.3034 20.2515 41.8082 19.5981 30.3148 19.7311C21.6027 19.8319 12.6614 19.761 4.02627 21.1498C3.02615 21.3107 0.292285 21.6899 1.1718 22.1925C4.43124 24.055 8.59367 24.2614 12.2308 24.5342C21.2009 25.2069 30.1938 23.446 39.0492 22.2608C42.0968 21.853 45.1409 21.491 48.1596 20.9105C49.3359 20.6843 46.4594 21.925 46.399 21.9532" stroke="#FFB800" stroke-linecap="round"/>
                </svg>
            </div>
    `
    const main = document.getElementById("main")
    const optimise = document.getElementById("optimise-main")
    main.removeChild(optimise)
    body.removeChild(monitor)
    body.classList.toggle("blur")

    main.appendChild(visualise)
    visualise.querySelector(".result").innerHTML = "Output: "+ max

}


