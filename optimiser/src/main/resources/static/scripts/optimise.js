const form = document.getElementById("form")
const monitor = document.getElementById("monitor")
const body = document.body
let i = 0
let plotData = [];

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

    //create an object to store form data
    const jsonData = {};
    for (const [key, value] of data.entries()) {
        jsonData[key] = value;
    }

    // Connect to the WebSocket
    const socket = new WebSocket("ws://localhost:8080/optimise-connect");
    socket.onopen = (event) => {
        // send the JSON data as a string
        socket.send(JSON.stringify(jsonData));
    };

    socket.onmessage = (event) => {
        console.log(event.data);
        plotData.push({ generation: ++i, fitness: parseFloat(event.data) });
        updateChart(plotData);
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

    // Add x-axis label
    svg.append('text')
        .attr('transform', 'translate(' + (width / 2) + ',' + (height + margin.bottom - 5) + ')')
        .style('text-anchor', 'middle')
        .style('font-size', '14px')
        .style('font-family', 'Montserrat') // Add this line
        .text('Generation');

// Add y-axis label
    svg.append('text')
        .attr('y', 0 - margin.left + 20)
        .attr('x', 0 - (height / 2))
        .attr('dy', '1em')
        .style('text-anchor', 'middle')
        .style('font-size', '14px')
        .style('font-family', 'Montserrat') // Add this line
        .text('Fitness');

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


