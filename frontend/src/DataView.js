import {useEffect, useState} from "react";
import {api} from "./apiRequest";


const DataView = ({name, assetType}) => {
    const [stockOpen, setStockOpen] = useState(undefined);
    const [stockClose, setStockClose] = useState(undefined);
    const [qty, setQty] = useState(undefined);
    useEffect(() => {
        const controller = new AbortController();
        fetchData(controller.signal, name, assetType).then(r => {
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
                        await handleBuy(name, "MARKET", qty, setQty, assetType)
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

const fetchData = async (signal, name, type) => {
    try {
        const url = "/api/v1/asset/" + type + "/" + name;
        const response = await api(url, {signal});
        console.log(response)
        return response.data

    } catch (err) {
        console.log(err)
        return [];
    }
}

const handleBuy = async (name, type, qty, setQty, assetType) => {
    // e.preventDefault();
    const order = {symbol: name, type: type, qty: qty}
    const url = "/api/v1/order/" + assetType;
    try {
        const response = await api.post(url, order);
        if (response.status > 300) {
            setQty(undefined);
        }
    } catch (err) {
        console.log(err)
    }
}


export default DataView;