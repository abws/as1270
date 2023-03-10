import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import { Sky } from 'three/addons/objects/Sky.js';
import { Water } from 'three/addons/objects/Water.js';
import Stats from 'three/addons/libs/stats.module.js';
import { addTurbine } from './components/turbine.js';

let container, sizes, renderer, scene, camera, mesh, controls, stats, water, sky, sun;

setUp();
animate();

//set up function 
function setUp () {
    container = document.querySelector('.wind-farm');

    sizes = {
        width: window.innerWidth,
        height: window.innerHeight
    }

    const fov = 55;
    const aspect = sizes.width / sizes.height;
    const near = 1;   //front clipping plane
    const far = 20000;    //back clipping plane - these two determine the depth of the scene (i.e. how far away objects can be from the camera (called the viewing frustum))


    //renderer
    renderer = new THREE.WebGLRenderer();
    renderer.setPixelRatio( window.devicePixelRatio );
    renderer.setSize( window.innerWidth, window.innerHeight );
    renderer.toneMapping = THREE.ACESFilmicToneMapping;
    container.appendChild( renderer.domElement );


    //scene
    scene = new THREE.Scene();
    camera = new THREE.PerspectiveCamera( fov, aspect, near, far );
    camera.position.set( 20, 20, 150 );

    //sun and water
    sun = new THREE.Vector3();
    const waterGeometry = new THREE.PlaneGeometry( 10000, 10000 );
    water = new Water( 
        waterGeometry,
        {
            textureWidth: 512,
            textureHeight: 512,
            waterNormals: new THREE.TextureLoader().load( 'scripts/waternormals.jpg', function ( texture ) {

                texture.wrapS = texture.wrapT = THREE.RepeatWrapping;

            } ),
            sunDirection: new THREE.Vector3(),
            sunColor: 0xffffff,
            waterColor: 0x001e0f,
            distortionScale: 3.7,
            fog: scene.fog !== undefined
        }
    );
    water.material.uniforms.size.value = 5;
    water.rotation.x = - Math.PI / 2;

    scene.add( water ); 

    //sky
    sky = new Sky();
    sky.scale.setScalar( 10000 );
    scene.add( sky );

    const skyUniforms = sky.material.uniforms;

    skyUniforms[ 'turbidity' ].value = 10;
    skyUniforms[ 'rayleigh' ].value = 2;
    skyUniforms[ 'mieCoefficient' ].value = 0.005;
    skyUniforms[ 'mieDirectionalG' ].value = 0.8;

    const parameters = {
        elevation: 2,
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
    controls.maxPolarAngle = Math.PI * 0.495;
    controls.update();


    //stats for nerds
    stats = new Stats();
    container.appendChild( stats.dom );

    window.addEventListener( 'resize', onWindowResize );

    //add turbines
    const coordinates = [[0, 0], [120, 100], [500, 600], [350, 725]]
    for (let i = 0; i < 3; i++) {
        addTurbineToScene(coordinates[i][0], coordinates[i][1]);
    }

}

//animation
function animate() {
    requestAnimationFrame( animate );
    render();
    stats.update();

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


