import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';

const canvas = document.querySelector(".wind-farm");

const scene = new THREE.Scene();
const geometry = new THREE.SphereGeometry(3, 64, 64);
const material = new THREE.MeshStandardMaterial({
    color: "#00ff83",
});
const shape = new THREE.Mesh(geometry, material);
scene.add(shape);

const sizes = {
    width: canvas.clientWidth,
    height: canvas.clientHeight
}

//lighting
const light = new THREE.PointLight(0xffffff, 1.25, 100)
light.position.set(0, 10, 10)
scene.add(light)

//camera
const camera = new THREE.PerspectiveCamera(45, sizes.width / sizes.height);
camera.position.z = 10;

scene.add(camera);

//render
const renderer = new THREE.WebGLRenderer({ canvas });

renderer.setSize(sizes.width, sizes.height);

renderer.render(scene, camera);

const loader = new GLTFLoader();

loader.load( 'models/turbine.gltf', function ( gltf ) {
    gltf.scene.scale.set(0.1, 0.1, 0.1)
	scene.add( gltf.scene );

}, undefined, function ( error ) {

	console.error( error );

} );

//orbit control
const controls = new OrbitControls(camera, canvas);
controls.enableDamping = true;
controls.enablePan = false;
controls.enableZoom = false;
controls.autoRotate = true;
controls.autoRotateSpeed = 5

//resize
// window.addEventListener("resize", () => {

//     camera.aspect = 800, 600;
//     camera.updateProjectionMatrix()
//     renderer.setSize(800, 600);
// })

const loop = () => {
    controls.update();
    shape.rotation.y += 0.1
    renderer.render(scene, camera)
    window.requestAnimationFrame(loop)
}

loop()