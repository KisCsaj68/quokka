import {useEffect, useState} from "react";
import {api} from "./apiRequest";
import DataView from "./DataView";
import "./Table.css"

const Data = ({type}) => {
    const [stocks, setStocks] = useState([]);
    useEffect(() => {
        const controller = new AbortController();
        fetchSymbols(controller.signal, type).then(r => setStocks(r))
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
                    <th><h1>Quantity</h1></th>
                    <th><h1></h1></th>
                </tr>
                </thead>
                <tbody>
                    {stocks.map((name) =>
                        <DataView key={name} name={name} assetType={type}/>)}
                </tbody>
            </table>
        </div>
    )
}

const fetchSymbols = async (signal, type) => {
    try {
        const url = "/api/v1/asset/" + type;
        const response = await api(url, {signal});
        console.log(response);
        if (response.status < 300) {
            return response.data
            ;
        }
    } catch (err) {
        console.log(err);
        return [];
    }
}


export default Data;