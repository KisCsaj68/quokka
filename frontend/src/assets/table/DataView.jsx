import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getAssetData } from "utils/apiRequest";

const DataView = ({ name, assetType }) => {
    const [stockOpen, setStockOpen] = useState(undefined);
    const [stockClose, setStockClose] = useState(undefined);
    const [qty, setQty] = useState(undefined);

    useEffect(() => {
        const controller = new AbortController();
        const fetchData = async () => {
            const data = await getAssetData(controller.signal, name, assetType);
            setStockOpen(data.open);
            setStockClose(data.close);
        };
        fetchData();
        return () => controller.abort();
    }, []);

    if (stockOpen === undefined || stockClose === undefined) {
        return (
            <tr>
                <td>Loading...</td>
            </tr>
        );
    } else {
        return (
            <tr>
                <td>{name}</td>
                <td>{stockOpen}</td>
                <td>{stockOpen < stockClose ? "⬆" : "⬇"}</td>
                <td>
                    <input
                        type="number"
                        name="quantity"
                        min="1"
                        onChange={(e) => setQty(e.target.value)}
                    />
                </td>
                <td>
                    {/* <button onClick={async () => {
                        await handleBuy(name, "MARKET", qty, setQty, assetType)
                    }}>Buy
                    </button> */}
                    <Link to={`/purchase/${assetType}/${name}`}>
                        <button>BUY</button>
                    </Link>
                </td>
            </tr>
        );
    }
};

export default DataView;
