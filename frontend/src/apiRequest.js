import axios from 'axios';
import useAuth from "./hooks/useAuth";

const BASE_URL = "http://localhost:3000"
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

const api = axios.create({
    baseURL: `${BASE_URL}`
});

export const privateApi = axios.create({
    baseURL: `${BASE_URL}`
})

export default api;

