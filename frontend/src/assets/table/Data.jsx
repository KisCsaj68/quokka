import { useEffect, useState } from "react";
import { getAssetSymbols } from "utils/apiRequest.js";
import DataView from "./DataView";
import "./table.css";

const Data = ({ type }) => {
    const [stocks, setStocks] = useState([]);
    useEffect(() => {
        const controller = new AbortController();
        getAssetSymbols(controller.signal, type).then((r) => setStocks(r));
        return () => controller.abort();
    }, []);

    return (
        <div className="regContainer">
            <table className="table_container">
                <thead>
                    <tr>
                        <th>
                            <h1>Name</h1>
                        </th>
                        <th>
                            <h1>Price</h1>
                        </th>
                        <th>
                            <h1>Trend</h1>
                        </th>
                        <th>
                            <h1>Quantity</h1>
                        </th>
                        <th>
                            <h1></h1>
                        </th>
                    </tr>
                </thead>
                <tbody>
                    {stocks.map((name) => (
                        <DataView key={name} name={name} assetType={type} />
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default Data;
