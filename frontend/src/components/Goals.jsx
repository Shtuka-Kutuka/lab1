import { useEffect, useState } from 'react';
import { getGoals, createGoal, updateGoal, deleteGoal } from '../api/api';

export default function Goals() {
    const [goals, setGoals] = useState([]);
    const [newTitle, setNewTitle] = useState('');
    const [goalsOpen, setGoalsOpen] = useState(true);
    const [completedOpen, setCompletedOpen] = useState(true);

    const loadGoals = async () => {
        const res = await getGoals();
        setGoals(res.data);
    };

    useEffect(() => {
        loadGoals();
    }, []);

    const addGoal = async () => {
        if (!newTitle.trim()) return;
        await createGoal({
            userId: 1,
            title: newTitle,
            targetDate: new Date().toISOString().slice(0,10),
            achieved: false
        });
        setNewTitle('');
        loadGoals();
    };

    const toggleAchieved = async (goal) => {
        await updateGoal(goal.id, { ...goal, achieved: !goal.achieved });
        loadGoals();
    };

    const handleDelete = async (id) => {
        await deleteGoal(id);
        loadGoals();
    };

    const deleteAllActive = async () => {
        const active = goals.filter(g => !g.achieved);
        for (let g of active) await deleteGoal(g.id);
        loadGoals();
    };

    const deleteAllCompleted = async () => {
        const completed = goals.filter(g => g.achieved);
        for (let g of completed) await deleteGoal(g.id);
        loadGoals();
    };

    const activeGoals = goals.filter(g => !g.achieved);
    const completedGoals = goals.filter(g => g.achieved);

    return (
        <>
            <div className="goals-wrapper">
                <div className="goals-header" onClick={() => setGoalsOpen(!goalsOpen)}>
                    <span>🎯 текущие цели</span>
                    <span>{goalsOpen ? '▲' : '▼'}</span>
                </div>
                <div className={`goals-content ${goalsOpen ? 'open' : ''}`}>
                    {activeGoals.map(goal => (
                        <div key={goal.id} className="goal-item">
                            <div
                                className={`checkbox-custom ${goal.achieved ? 'checked' : ''}`}
                                onClick={() => toggleAchieved(goal)}
                            ></div>
                            <div className="goal-info">
                                <div className="goal-title">{goal.title}</div>
                                <div className="goal-date">создана: {goal.targetDate}</div>
                            </div>
                            <button className="delete-goal-btn" onClick={() => handleDelete(goal.id)}>🗑️</button>
                        </div>
                    ))}
                    <div className="add-goal">
                        <input
                            type="text"
                            placeholder="новая цель..."
                            value={newTitle}
                            onChange={e => setNewTitle(e.target.value)}
                        />
                        <button className="small-btn" onClick={addGoal} style={{ width: 'auto' }}>+</button>
                    </div>
                    <button className="dark-btn" onClick={deleteAllActive}>🗑️ удалить все цели</button>
                </div>
            </div>

            <div className="completed-goals-wrapper">
                <div className="completed-header" onClick={() => setCompletedOpen(!completedOpen)}>
                    <span>✅ выполненные цели</span>
                    <span>{completedOpen ? '▲' : '▼'}</span>
                </div>
                <div className={`completed-content ${completedOpen ? 'open' : ''}`}>
                    {completedGoals.map(goal => (
                        <div key={goal.id} className="completed-item">
                            <div className="goal-info">
                                <div className="goal-title">{goal.title}</div>
                                <div className="goal-date">создана: {goal.targetDate}</div>
                            </div>
                            <button className="delete-goal-btn" onClick={() => handleDelete(goal.id)}>🗑️</button>
                        </div>
                    ))}
                    <button className="dark-btn" onClick={deleteAllCompleted}>🗑️ удалить выполненные</button>
                </div>
            </div>
        </>
    );
}