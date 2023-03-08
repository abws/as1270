import * as THREE from 'three';
import { OrbitControls } from 'three/addons/OrbitControls.js';
const scene = new THREE.Scene();
const geometry = new THREE.SphereGeometry(3, 64, 64);
const material = new THREE.MeshStandardMaterial({
    color: "#00ff83",
});
const shape = new THREE.Mesh(geometry, material);
scene.add(shape);

//sizes
const sizes = {
    width: window.innerWidth,
    height: window.innerHeight
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
const canvas = document.querySelector(".wind-farm");
const renderer = new THREE.WebGLRenderer({ canvas });

renderer.setSize(sizes.width, sizes.height);

renderer.render(scene, camera);

//orbit control
const controls = new OrbitControls(camera, canvas);
controls.enableDamping = true;
controls.enablePan = false;
controls.enableZoom = false;
controls.autoRotate = true;
controls.autoRotateSpeed = 5

//resize
window.addEventListener("resize", () => {

    camera.aspect = 800, 600;
    camera.updateProjectionMatrix()
    renderer.setSize(800, 600);
})

const loop = () => {
    controls.update();
    shape.rotation.y += 0.1
    renderer.render(scene, camera)
    window.requestAnimationFrame(loop)
}

loop()