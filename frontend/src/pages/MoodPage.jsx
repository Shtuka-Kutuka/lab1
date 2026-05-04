import { useEffect, useState } from "react";
import { getMoods, deleteMood } from "../api/api";
import MoodForm from "../components/MoodForm";
import Filter from "../components/Filter";

export default function MoodPage() {
    const [moods, setMoods] = useState([]);

    const load = () => {
        getMoods().then(res => setMoods(res.data));
    };

    useEffect(load, []);

    const remove = async (id) => {
        await deleteMood(id);
        load();
    };

    return (
        <div className="container">
            <h2>Настроение</h2>

            <MoodForm />
            <Filter />

            <h3>Все записи</h3>

            {moods.map(m => (
                <div key={m.id}>
                    {m.mood} | {m.date}
                    <button onClick={() => remove(m.id)}>Удалить</button>
                </div>
            ))}
        </div>
    );
}