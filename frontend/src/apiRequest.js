import axios from 'axios';

const BASE_URL = "http://localhost"
// export default axios.create({
//     baseURL: 'http://localhost:3000',
//     headers:{
//         'Accept': 'application/json',
//         'Content-Type': 'application/json',
//         'Access-Control-Allow-Origin': '*',
//     },


// });
axios.defaults.headers["Accept"] = "application/json";
axios.defaults.headers["Content-Type"] = "application/json";
axios.defaults.headers["Access-Control-Allow-Origin"] = "*";

export const regApi = axios.create({
    baseURL: `${BASE_URL}:8080`
});

export const assetApi = axios.create({
    baseURL: `${BASE_URL}:8000`
});


