const form = document.querySelector('#form');





form.addEventListener('submit', (event) => {
    event.preventDefault();
    const data = new FormData(form);
    fetch('http://localhost:8080/optimise', {
        method: 'POST',
        body: JSON.stringify(data),
        headers: {
            'content-type': 'application/json'
        }
    }).then(response => response.json())
        .then(result => {
            console.log(result);
        });
    const sse = new EventSource('http://localhost:8080/sse');
    sse.onmessage = function (evt) {
        console.log(evt.data)
    };
});

