import axios from "axios";

const BASE_URL = "http://localhost:3000";
const BASE_API_PATH = "/api/v1";

axios.defaults.headers["Accept"] = "application/json";
axios.defaults.headers["Content-Type"] = "application/json";
axios.defaults.headers["Access-Control-Allow-Origin"] = "*";

export const api = axios.create({
    baseURL: `${BASE_URL}`,
});

export const getAssetData = async (signal, name, type) => {
    const url = `${BASE_API_PATH}/asset/${type}/${name}`;
    try {
        const resp = await api.get(url, { signal });
        if (resp.status === 200) {
            return resp.data;
        }
    } catch (err) {
        return {};
    }
    return {};
};

export const getAssetSymbols = async (signal, type) => {
    const url = `${BASE_API_PATH}/asset/${type}`;
    try {
        const resp = await api.get(url, { signal });
        if (resp.status < 300) {
            return resp.data;
        }
    } catch (err) {
        return [];
    }
    return [];
};
const postBuyAsset = async (orderObj) => {
    const url = `${BASE_API_PATH}/order/${orderObj.assetType.toLowerCase()}`;
    try {
        const resp = await api.post(url, orderObj);
        if (resp.ok) {
            return resp.json();
        }
    } catch (err) {
        return {};
    }
    return {};
};
export const postBuyMarketAsset = async (assetType, quantity, symbol) => {
    const order = {
        quantity: quantity,
        symbol: symbol,
        type: "MARKET",
        assetType: assetType.toUpperCase(),
    };
    return await postBuyAsset(order);
};

export const postBuyLimitAsset = async (assetType, quantity, symbol, limit) => {
    const order = {
        quantity: quantity,
        symbol: symbol,
        type: "LIMIT",
        limit: limit,
        assetType: assetType.toUpperCase(),
    };
    return await postBuyAsset(order);
};

export const buyStockMarket = async (quantity, symbol) => {
    return await postBuyMarketAsset("STOCK", quantity, symbol);
};
export const buyStockLimit = async (quantity, symbol, limit) => {
    return await postBuyLimitAsset("STOCK", quantity, symbol), limit;
};

export const buyCryptoMarket = async (quantity, symbol) => {
    return await postBuyMarketAsset("CRYPTO", quantity, symbol);
};
export const buyCryptoLimit = async (quantity, symbol, limit) => {
    return await postBuyLimitAsset("CRYPTO", quantity, symbol), limit;
};

export const postNewUser = async (newUser) => {
    const url = `${BASE_API_PATH}/user`;
    try {
        const resp = await api.post(url, newUser);
        if (resp.status === 200) {
            return newUser;
        }
    } catch (err) {
        return err.response.request.response;
    }
};
