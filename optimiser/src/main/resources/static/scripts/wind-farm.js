// import * as THREE from 'three';
// import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
// import { BoxGeometry } from 'home/src/geometries/BoxGeometry.js';
// import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';
// import { Sky } from 'three/addons/objects/Sky.js';
// import { Water } from 'three/addons/objects/Water.js';
//
//
// //set up preliminaries
// const canvas = document.querySelector(".wind-farm");
// const sizes = {
//     width: canvas.clientWidth,
//     height: canvas.clientHeight
// }
// const fov = 60;
// const aspect = sizes.width / sizes.height;
// const near = 1;   //front clipping plane
// const far = 200000;    //back clipping plane - these two determine the depth of the scene (i.e. how far away objects can be from the camera (called the viewing frustum))
//
// //init renderer
// const renderer = new THREE.WebGLRenderer({ canvas });
//
// //world settings
// const effectController = {
//     turbidity: 10,
//     rayleigh: 3,
//     mieCoefficient: 0.005,
//     mieDirectionalG: 0.7,
//     elevation: 1,
//     azimuth: 180,
//     exposure: renderer.toneMappingExposure
// };
//
// //create scene and set sky and sun
// const scene = new THREE.Scene();
// const sky = new Sky();
// sky.scale.setScalar( 450000 );
// scene.add( sky );
//
// const sun = new THREE.Vector3();
// const uniforms = sky.material.uniforms;
// uniforms[ 'turbidity' ].value = effectController.turbidity;
// uniforms[ 'rayleigh' ].value = effectController.rayleigh;
// uniforms[ 'mieCoefficient' ].value = effectController.mieCoefficient;
// uniforms[ 'mieDirectionalG' ].value = effectController.mieDirectionalG;
//
// const phi = THREE.MathUtils.degToRad( 90 - effectController.elevation );
// const theta = THREE.MathUtils.degToRad( effectController.azimuth );
// sun.setFromSphericalCoords( 1, phi, theta );
// uniforms[ 'sunPosition' ].value.copy( sun );
//
// renderer.toneMappingExposure = 0.5;
// renderer.toneMappingExposure = effectController.exposure;
// renderer.outputEncoding = THREE.sRGBEncoding;
// renderer.toneMapping = THREE.ACESFilmicToneMapping;
// renderer.setSize(sizes.width, sizes.height);
//
//
// //lighting
// // const light = new THREE.AmbientLight(0xffffff, 0.5)
// // scene.add(light)
//
// //camera
// const camera = new THREE.PerspectiveCamera( fov, aspect, near, far );
// camera.position.set( 0, 100, 2000 );
// scene.add(camera);
//
// //grid
// // const gridHelper = new THREE.GridHelper(1000, 10);
// // scene.add(gridHelper);
//
// //redner
// render();
//
// function render() {
//     renderer.render(scene, camera);
// }
//
// //allow moving around farm
// const controls = new OrbitControls( camera, canvas );
// controls.addEventListener( 'change', render );
// controls.enableZoom = true;
// controls.enablePan = true;
//
//
//
// const loader = new GLTFLoader();
//
// loader.load( 'models/turbine.gltf', function ( gltf ) {
//     gltf.scene.scale.set(10, 10, 10)
//     gltf.scene.position.set(100, 0, 1000)
// 	scene.add( gltf.scene );
// }, undefined, function ( error ) {
// 	console.error( error );
// } );