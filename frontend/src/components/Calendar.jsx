import { useState, useEffect } from 'react';
import { getMoodsByUser, getMoodTypes } from '../api/api';

export default function Calendar({ onSelectDate, selectedDate, refreshTrigger }) {
    const [currentYear, setCurrentYear] = useState(2026);
    const [currentMonth, setCurrentMonth] = useState(4);
    const [moodsByDate, setMoodsByDate] = useState({});
    const [moodEmojiMap, setMoodEmojiMap] = useState({});
    const userId = 1;


    const loadMoodTypes = async () => {
        try {
            const res = await getMoodTypes();
            const map = {};
            res.data.forEach(mt => {
                map[mt.name] = mt.emoji || '😐';
            });
            setMoodEmojiMap(map);
        } catch (err) {
            console.warn("Failed to load mood types", err);

            setMoodEmojiMap({
                "Радость": "😊", "Раздражение": "😤", "Тревога": "😟", "Скука": "😑",
                "Удивление": "😲", "Восторг": "🤩", "Спокойствие": "😌", "Грусть": "😔",
                "Злость": "😠", "Страх": "😨", "Интерес": "🤔", "Вина": "😣",
                "Надежда": "🙂", "Вдохновение": "✨", "Усталость": "😴", "Любовь": "🥰",
                "Стыд": "😳", "Эйфория": "🤪", "Ностальгия": "🥺", "Гордость": "😌",
                "Благодарность": "🙏"
            });
        }
    };

    const loadMoods = async () => {
        try {
            const res = await getMoodsByUser(userId);
            const entries = res.data;
            const grouped = {};
            entries.forEach(entry => {
                const date = entry.date;
                if (!grouped[date]) grouped[date] = [];
                grouped[date].push(entry.mood);
            });
            setMoodsByDate(grouped);
        } catch (err) {
            console.error("Failed to load moods", err);
        }
    };

    useEffect(() => {
        loadMoodTypes();
    }, []);

    useEffect(() => {
        loadMoods();
    }, [refreshTrigger]);

    const changeMonth = (delta) => {
        let newMonth = currentMonth + delta;
        let newYear = currentYear;
        if (newMonth < 0) {
            newMonth = 11;
            newYear--;
        } else if (newMonth > 11) {
            newMonth = 0;
            newYear++;
        }
        setCurrentYear(newYear);
        setCurrentMonth(newMonth);
    };

    const getEmojiForMood = (moodName) => {
        return moodEmojiMap[moodName] || '😐';
    };

    const renderCalendar = () => {
        const monthNames = ["Январь","Февраль","Март","Апрель","Май","Июнь","Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь"];
        const firstDay = new Date(currentYear, currentMonth, 1);
        let startWeekday = firstDay.getDay();
        let startOffset = (startWeekday === 0 ? 6 : startWeekday - 1);
        const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();

        const days = [];
        for (let i = 0; i < startOffset; i++) {
            days.push(<div key={`empty-${i}`} className="day-cell" style={{ opacity: 0.2 }}></div>);
        }
        for (let d = 1; d <= daysInMonth; d++) {
            const dateStr = `${currentYear}-${String(currentMonth+1).padStart(2,'0')}-${String(d).padStart(2,'0')}`;
            const moodList = moodsByDate[dateStr] || [];

            const emojis = moodList.map(mood => getEmojiForMood(mood)).join(' ');
            const hasMood = moodList.length > 0;
            days.push(
                <div
                    key={d}
                    className={`day-cell ${hasMood ? 'has-mood' : ''}`}
                    onClick={() => onSelectDate(dateStr)}
                >
                    <div className="day-number">{d}</div>
                    <div className="day-emojis">{emojis || '○'}</div>
                </div>
            );
        }
        return days;
    };

    return (
        <div className="calendar-grid">
            <div className="calendar-header">
                <button className="month-nav" onClick={() => changeMonth(-1)}>←</button>
                <div className="month-name">
                    {["Январь","Февраль","Март","Апрель","Май","Июнь","Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь"][currentMonth]} {currentYear}
                </div>
                <button className="month-nav" onClick={() => changeMonth(1)}>→</button>
            </div>
            <div className="weekdays">
                <span>Пн</span><span>Вт</span><span>Ср</span><span>Чт</span><span>Пт</span><span>Сб</span><span>Вс</span>
            </div>
            <div className="days-grid">
                {renderCalendar()}
            </div>
        </div>
    );
}