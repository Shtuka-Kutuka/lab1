import { useEffect, useState } from "react";
import { getUsers, createUser } from "../api/api";

export default function UsersPage() {
    const [users, setUsers] = useState([]);
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");

    const load = () => getUsers().then(res => setUsers(res.data));

    useEffect(load, []);

    const add = async () => {
        await createUser({ username, email });
        load();
    };

    return (
        <div>
            <h2>Users</h2>

            <input onChange={e => setUsername(e.target.value)} placeholder="username"/>
            <input onChange={e => setEmail(e.target.value)} placeholder="email"/>

            <button onClick={add}>Create</button>

            {users.map(u => (
                <div key={u.id}>
                    {u.username} ({u.email})
                </div>
            ))}
        </div>
    );
}