import axios from 'axios';

// TODO: use express app to set-up upstream proxying instead of `proxy` field in package.json
const { REACT_APP_HOST, REACT_APP_PORT } = process.env

const BASE_URL = `http://${REACT_APP_HOST}:${REACT_APP_PORT}`

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

