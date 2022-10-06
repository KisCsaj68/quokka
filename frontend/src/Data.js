import {useEffect, useState} from "react";
import {assetApi} from "./apiRequest";
import StockView from "./StockView";
import "./Table.css"

const Stock = () => {
    const [stocks, setStocks] = useState([]);
    useEffect(() => {
        const controller = new AbortController();
        fetchSymbols(controller.signal).then(r => setStocks(r))
        return () => controller.abort();
    }, [])

    return (
        <div className="regContainer">
            <table class="table_container">
                <thead>
                <tr>
                    <th><h1>Name</h1></th>
                    <th><h1>Price</h1></th>
                    <th><h1>Trend</h1></th>
                </tr>
                </thead>
                <tbody>
                    {stocks.map((name) =>
                        <StockView key={name} name={name}/>)}
                </tbody>
            </table>
        </div>
    )
}

const fetchSymbols = async (signal) => {
    try {
        const response = await assetApi("/api/v1/stock", {signal});
        if (response.status < 300) {
            return response.data.stock;
        }
    } catch (err) {
        console.log(err);
        return [];
    }
}


export default Stock;