import { useState } from "react";
import { filterMoods } from "../api/api";

export default function Filter() {
    const [userId, setUserId] = useState(1);
    const [mood, setMood] = useState("");
    const [result, setResult] = useState([]);

    const search = async () => {
        const res = await filterMoods(userId, mood);
        setResult(res.data.content);
    };

    return (
        <div>
            <h3>Фильтр</h3>

            <input onChange={e => setUserId(e.target.value)} placeholder="userId"/>
            <input onChange={e => setMood(e.target.value)} placeholder="mood"/>

            <button onClick={search}>Search</button>

            {result.map(r => (
                <div key={r.id}>
                    {r.mood} - {r.date}
                </div>
            ))}
        </div>
    );
}