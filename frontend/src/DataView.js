import {useEffect, useState} from "react";
import useAxiosPrivate from "./hooks/useAxiosPrivate";


const DataView = ({name, assetType}) => {
    const [stockOpen, setStockOpen] = useState(undefined);
    const [stockClose, setStockClose] = useState(undefined);
    const [qty, setQty] = useState(undefined);
    const privateApi = useAxiosPrivate();
    useEffect(() => {
        const controller = new AbortController();
        fetchData(controller.signal, name, assetType, privateApi).then(r => {
            setStockOpen(r.open)
            setStockClose(r.close)
        });
        return () => controller.abort();

    }, [qty])
    if (stockOpen || stockClose !== undefined) {
        return (
            <tr>
                <td>{name}</td>
                <td>{stockOpen}</td>
                <td>{stockOpen < stockClose ? "⬆" : "⬇"}</td>
                <td><input type="number" name="quantity" min="1" onChange={(e) => setQty(e.target.value)}/></td>
                <td>
                    <button onClick={async () => {
                        await handleBuy(name, "MARKET", qty, setQty, assetType, privateApi)
                    }}>Buy
                    </button>
                </td>
            </tr>
        )
    } else {
        return (
            <tr>
                <td>Loading...</td>

            </tr>)
    }

}

const fetchData = async (signal, name, type, privateApi) => {
    try {
        const url = "/api/v1/asset/" + type + "/" + name;
        const response = await privateApi.get(url, {signal});
        return response.data

    } catch (err) {
        console.log(err)
        return [];
    }
}

const handleBuy = async (name, type, qty, setQty, assetType, privateApi) => {
    // e.preventDefault();
    const order = {symbol: name, type: type, qty: qty, side: "BUY"}
    const url = "/api/v1/order/" + assetType;
    try {
        const response = await privateApi.post(url, order);
        if (response.status > 300) {
            setQty("");
        }
    } catch (err) {
        console.log(err)
    }
}


export default DataView;