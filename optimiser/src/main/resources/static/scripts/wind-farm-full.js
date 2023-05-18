import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import { Sky } from 'three/addons/objects/Sky.js';
import { Water } from 'three/addons/objects/Water.js';
import Stats from 'three/addons/libs/stats.module.js';
import { addTurbine } from './components/turbine.js';

const input = document.getElementById('coordinates');
const toggle = document.getElementById('toggle');

toggle.addEventListener('click', (e) => {
    if (input.style.display === 'none') {
        input.style.display = 'block';
        toggle.classList.add('reverse');
    }
    else {
        input.style.display = 'none';
        toggle.classList.remove('reverse');

    }
});

if (localStorage.getItem('coordinates')) {
    input.value = localStorage.getItem('coordinates');
}
else {
    input.value = '[]';
    localStorage.setItem('coordinates', JSON.stringify([]));
}

input .addEventListener('change', (e) => {
    console.log(e.target.value)
    //store coordinates in local storage, while also changing it from how it was in input field
    localStorage.setItem('coordinates', JSON.stringify(JSON.parse(e.target.value)));



    location.reload();
});

let container, sizes, renderer, scene, camera, mesh, controls, stats, water, sky, sun, turbines, farmLength, farmWidth;

setUp();
animate();

//set up function 
function setUp () {
    container = document.querySelector('.wind-farm');
    turbines = JSON.parse(localStorage.getItem('coordinates'))
    console.log(turbines)


    sizes = {
        width: window.innerWidth,
        height: window.innerHeight
    }

    const fov = 55;
    const aspect = sizes.width / sizes.height;
    // these two determine the depth of the scene (i.e. how far away objects can be from the camera (called the viewing frustum))
    const near = 1;   //front clipping plane
    const far = 200000;    //back clipping plane


    //renderer
    renderer = new THREE.WebGLRenderer();
    renderer.setPixelRatio( window.devicePixelRatio );
    renderer.setSize( window.innerWidth, window.innerHeight );
    renderer.toneMapping = THREE.ACESFilmicToneMapping;
    container.appendChild( renderer.domElement );


    //scene
    scene = new THREE.Scene();
    camera = new THREE.PerspectiveCamera( fov, aspect, near, far );
    camera.position.set( 2000, 5000, -5000 );

    //sun and water
    sun = new THREE.Vector3();
    const waterGeometry = new THREE.PlaneGeometry( 500000, 500000 );
    water = new Water( 
        waterGeometry,
        {
            textureWidth: 512,
            textureHeight: 512,
            waterNormals: new THREE.TextureLoader().load( 'scripts/waternormals.jpg', function ( texture ) {

                texture.wrapS = texture.wrapT = THREE.RepeatWrapping;

            } ),
            sunDirection: new THREE.Vector3(),
            sunColor: 0xFFFFAD,
            waterColor: 0x025ab3,
            distortionScale: 3.7,
            fog: scene.fog !== undefined
        }
    );
    water.material.uniforms.size.value = 0.1;
    water.rotation.x = - Math.PI / 2;

    scene.add( water ); 

    //sky
    sky = new Sky();
    sky.scale.setScalar( 500000 );
    scene.add( sky );

    const skyUniforms = sky.material.uniforms;

    skyUniforms[ 'turbidity' ].value = 0.4;
    skyUniforms[ 'rayleigh' ].value = 0.4;
    skyUniforms[ 'mieCoefficient' ].value = 0.005;
    skyUniforms[ 'mieDirectionalG' ].value = 0.8;

    const parameters = {
        elevation: 8,
        azimuth: 180
    };

    const pmremGenerator = new THREE.PMREMGenerator( renderer );
    let renderTarget;

    //sun parameters
    function updateSun() {

        const phi = THREE.MathUtils.degToRad( 90 - parameters.elevation );
        const theta = THREE.MathUtils.degToRad( parameters.azimuth );

        sun.setFromSphericalCoords( 1, phi, theta );

        sky.material.uniforms[ 'sunPosition' ].value.copy( sun );
        water.material.uniforms[ 'sunDirection' ].value.copy( sun ).normalize();

        if ( renderTarget !== undefined ) renderTarget.dispose();

        renderTarget = pmremGenerator.fromScene( sky );

        scene.environment = renderTarget.texture;

    }

    updateSun();

    //allow movement around world 
    
    controls = new OrbitControls( camera, renderer.domElement );

    // controls.autoRotate = true;
    controls.autoRotateSpeed = 0.5;
    controls.enableDamping = true;
    controls.screenSpacePanning = false;
    controls.minDistance = 25
    controls.maxDistance = 5000

    controls.maxPolarAngle = Math.PI * 0.495;



    //stats for nerds
    stats = new Stats();
    container.appendChild( stats.dom );

    window.addEventListener( 'resize', onWindowResize );

    for (let i = 0; i < turbines.length; i++) {
        addTurbineToScene(turbines[i][0], turbines[i][1]);
    }

}

//animation
function animate() {
    requestAnimationFrame( animate );
    render();
    stats.update();
    controls.update();


}


//render
function render() { 

    const time = performance.now() * 0.001;
    water.position.y = sky.position.y - 100;

    water.material.uniforms[ 'time' ].value += 1.0 / 60.0;

    renderer.render( scene, camera );

}

//resizing function
function onWindowResize() {

    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();

    renderer.setSize( window.innerWidth, window.innerHeight );

}

async function addTurbineToScene(x, y) {
    const turbine = await addTurbine(x, y);
    scene.add(turbine.scene);
}