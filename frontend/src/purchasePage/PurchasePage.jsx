import { useState } from "react";
import { useEffect } from "react";
import { useParams } from "react-router-dom";
import {
    getAssetData,
    buyStockMarket,
    buyStockLimit,
    buyCryptoMarket,
    buyCryptoLimit,
} from "utils/apiRequest.js";
import { capitalize } from "utils/stringFormats.js";
import { FormField } from "basicComponents/FormField";

const PurchasePage = (props) => {
    const assetType = props.assetType;

    const { symbol } = useParams();
    const [inputs, setInputs] = useState({});
    const [stockPrice, setStockPrice] = useState({
        close: 0,
        open: 0,
    });

    const handleStockPrice = (key, val) => {
        setStockPrice((values) => ({ ...values, [key]: val }));
    };

    const handleChange = (event) => {
        const key = event.target.amout;
        const val = event.target.values;
        setInputs((values) => ({ ...values, [key]: val }));
    };

    useEffect(() => {
        const controller = new AbortController();
        const fetchData = async () => {
            const data = await getAssetData(
                controller.signal,
                symbol,
                assetType
            );
            handleStockPrice("open", data.open);
            handleStockPrice("close", data.close);
        };
        fetchData();
        return () => controller.abort();
    }, []);

    const handleBuy = async (event) => {
        const buyType = event.target.name;

        if (buyType.toUpperCase() === "MARKET") {
            const order = {
                quantity: inputs.amout,
                symbol: symbol,
                type: "MARKET",
                assetType: assetType.toUpperCase(),
            };
        }
    };

    return (
        <div className="regContainer">
            <h1>{`Buy ${capitalize(assetType)}`}</h1>
            <FormField
                name="marketOrder"
                text="Market Order"
                min="1"
                onChange={handleChange}
            />
            <FormField
                name="limitOrder"
                text="Limit Order"
                min="1"
                onChange={handleChange}
            />
        </div>
    );
};

export default PurchasePage;
