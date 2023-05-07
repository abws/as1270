import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';

function addTurbine(x, y) {
    const loader = new GLTFLoader();
    return new Promise((resolve, reject) => {
        loader.load('models/turbine.gltf', function (turbine) {
            turbine.scene.position.set(2*y, -100, 2*x);
            turbine.scene.scale.set(100, 100, 100);
            turbine.scene.rotation.y = Math.PI;
            resolve(turbine);
        }, undefined, reject);
    });
}

export { addTurbine };