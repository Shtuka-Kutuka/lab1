import { useState, useEffect } from 'react';
import Calendar from '../components/Calendar';
import MoodForm from '../components/MoodForm';
import MoodEntryView from '../components/MoodEntryView';
import Goals from '../components/Goals';
import { getMoodsByUser } from '../api/api';

export default function Dashboard() {
    const [selectedDate, setSelectedDate] = useState(new Date().toISOString().slice(0, 10));
    const [streak, setStreak] = useState(0);
    const [existingMoods, setExistingMoods] = useState([]);
    const [existingNote, setExistingNote] = useState('');
    const [isAddingMode, setIsAddingMode] = useState(false);
    const [refreshCalendar, setRefreshCalendar] = useState(0);
    const userId = 1;

    // ... функции calculateStreak, loadEntriesForDate, useEffect (те же)

    const calculateStreak = async () => {
        try {
            const res = await getMoodsByUser(userId);
            const dates = res.data.map(entry => entry.date).sort();
            if (!dates.length) return;
            const today = new Date().toISOString().slice(0, 10);
            let streakCount = 0;
            if (dates.includes(today)) {
                let cur = 1;
                let check = new Date(today);
                while (true) {
                    check.setDate(check.getDate() - 1);
                    const str = check.toISOString().slice(0, 10);
                    if (dates.includes(str)) cur++;
                    else break;
                }
                streakCount = cur;
            }
            setStreak(streakCount);
        } catch (err) {
            console.error(err);
        }
    };

    const loadEntriesForDate = async (date) => {
        try {
            const res = await getMoodsByUser(userId);
            const entriesForDate = res.data.filter(entry => entry.date === date);
            setExistingMoods(entriesForDate);
            const savedNote = localStorage.getItem(`note_${date}`);
            setExistingNote(savedNote || '');
            if (entriesForDate.length === 0) {
                setIsAddingMode(true);
            } else {
                setIsAddingMode(false);
            }
        } catch (err) {
            console.error('Failed to load entries', err);
            setExistingMoods([]);
            setExistingNote('');
            setIsAddingMode(true);
        }
    };

    useEffect(() => {
        calculateStreak();
    }, []);

    useEffect(() => {
        loadEntriesForDate(selectedDate);
    }, [selectedDate]);

    const handleMoodSaved = () => {
        calculateStreak();
        loadEntriesForDate(selectedDate);
        setIsAddingMode(false);
        setRefreshCalendar(prev => prev + 1);
    };

    const handleStartAdd = () => {
        setIsAddingMode(true);
    };

    const handleCancelAdd = () => {
        setIsAddingMode(false);
    };

    const handleNoteSaved = (newNote) => {
        localStorage.setItem(`note_${selectedDate}`, newNote);
        setExistingNote(newNote);
    };

    const handleDeleteAll = () => {
        loadEntriesForDate(selectedDate);
        setRefreshCalendar(prev => prev + 1);
    };

    const handleMoodDeleted = (deletedId) => {
        // Удаляем удалённую эмоцию из состояния
        setExistingMoods(prev => prev.filter(m => m.id !== deletedId));
        // Если после удаления не осталось эмоций, переключаемся в режим добавления
        if (existingMoods.length === 1) {
            setIsAddingMode(true);
        }
        // Обновляем календарь и серию
        setRefreshCalendar(prev => prev + 1);
        calculateStreak();
    };

    return (
        <div className="app-container">
            <div className="user-bar">
                <div className="greeting">☕ добрый день, Александра</div>
                <div className="streak">🔥 серия: <span>{streak}</span> дней</div>
            </div>

            <Calendar
                onSelectDate={setSelectedDate}
                selectedDate={selectedDate}
                refreshTrigger={refreshCalendar}
            />

            <div className="dashboard-row">
                <div className="mood-section">
                    {isAddingMode ? (
                        <MoodForm
                            selectedDate={selectedDate}
                            onMoodSaved={handleMoodSaved}
                            onCancel={handleCancelAdd}
                            initialNote={existingNote}
                            onNoteChange={handleNoteSaved}
                            showCancel={existingMoods.length > 0}
                        />
                    ) : (
                        <MoodEntryView
                            date={selectedDate}
                            moods={existingMoods}
                            note={existingNote}
                            onAddNew={handleStartAdd}
                            onNoteChange={handleNoteSaved}
                            onDeleteAll={handleDeleteAll}
                            onMoodDeleted={handleMoodDeleted}
                        />
                    )}
                </div>
                <div className="side-section">
                    <Goals />
                </div>
            </div>
            <footer>дневник настроения — несколько эмоций в день, теги и цели</footer>
        </div>
    );
}