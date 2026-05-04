import { useEffect, useState } from "react";
import { getTags, createTag } from "../api/api";

export default function Tags() {
    const [tags, setTags] = useState([]);
    const [name, setName] = useState("");
    const [color, setColor] = useState("");

    const load = () => {
        getTags().then(res => setTags(res.data));
    };

    useEffect(load, []);

    const add = async () => {
        await createTag({ name, color });
        setName("");
        setColor("");
        load();
    };

    return (
        <div>
            <h3>Теги</h3>

            <input
                placeholder="Название"
                value={name}
                onChange={e => setName(e.target.value)}
            />

            <input
                placeholder="Цвет"
                value={color}
                onChange={e => setColor(e.target.value)}
            />

            <button onClick={add}>Добавить</button>

            {tags.map(tag => (
                <div key={tag.id}>
                    <span style={{ color: tag.color }}>
                        {tag.name}
                    </span>
                    <small> (moods: {tag.moodEntryIds?.length || 0})</small>
                </div>
            ))}
        </div>
    );
}