import { useState, useEffect } from 'react';
import { createMood, getTags, getMoodTypes, createMoodType, deleteMoodType } from '../api/api';
import { useModal } from '../context/ModalContext';

// Начальный пул эмоций (fallback)
const fallbackMoods = [
    { name: "Радость", emoji: "😊" }, { name: "Раздражение", emoji: "😤" },
    { name: "Тревога", emoji: "😟" }, { name: "Скука", emoji: "😑" },
    { name: "Удивление", emoji: "😲" }, { name: "Восторг", emoji: "🤩" },
    { name: "Спокойствие", emoji: "😌" }, { name: "Грусть", emoji: "😔" },
    { name: "Злость", emoji: "😠" }, { name: "Страх", emoji: "😨" },
    { name: "Интерес", emoji: "🤔" }, { name: "Вина", emoji: "😣" },
    { name: "Надежда", emoji: "🙂" }, { name: "Вдохновение", emoji: "✨" },
    { name: "Усталость", emoji: "😴" }, { name: "Любовь", emoji: "🥰" },
    { name: "Стыд", emoji: "😳" }, { name: "Эйфория", emoji: "🤪" },
    { name: "Ностальгия", emoji: "🥺" }, { name: "Гордость", emoji: "😌" },
    { name: "Благодарность", emoji: "🙏" }
];

const extraEmojis = ["🤗", "😎", "🥳", "😇", "🥲", "🤯", "😶‍🌫️", "🫠", "🤠", "👽"];

export default function MoodForm({ selectedDate, onMoodSaved, onCancel, initialNote, onNoteChange, showCancel = true }) {
    const { showAlert, showConfirm } = useModal();
    const [moodsList, setMoodsList] = useState(fallbackMoods);
    const [selectedMood, setSelectedMood] = useState("Радость");
    const [tags, setTags] = useState([]);
    const [selectedTagIds, setSelectedTagIds] = useState([]);
    const [pickerOpen, setPickerOpen] = useState(false);
    const [moodSearch, setMoodSearch] = useState("");
    const [tagsPickerOpen, setTagsPickerOpen] = useState(false);
    const [tagSearch, setTagSearch] = useState("");
    const [note, setNote] = useState(initialNote || "");
    const [userId] = useState(1);
    const [showAddEmoji, setShowAddEmoji] = useState(false);
    const [newEmojiName, setNewEmojiName] = useState("");
    const [newEmojiSymbol, setNewEmojiSymbol] = useState("😊");
    const [deletingMoodId, setDeletingMoodId] = useState(null);

    const fetchMoodTypes = async () => {
        try {
            const res = await getMoodTypes();
            if (res.data && res.data.length) setMoodsList(res.data);
        } catch (err) {
            console.warn("Using fallback moods", err);
        }
    };

    useEffect(() => {
        const fetchData = async () => {
            try {
                const tagsRes = await getTags();
                setTags(tagsRes.data);
                await fetchMoodTypes();
            } catch (err) {
                console.warn("Initial fetch error", err);
            }
        };
        fetchData();
    }, []);

    useEffect(() => {
        setNote(initialNote || "");
    }, [initialNote]);

    const handleNoteChange = (e) => {
        const newNote = e.target.value;
        setNote(newNote);
        if (onNoteChange) onNoteChange(newNote);
    };

    const handleSaveMood = async () => {
        if (!selectedDate) {
            showAlert('Внимание', 'Выберите день в календаре');
            return;
        }
        try {
            await createMood({
                mood: selectedMood,
                date: selectedDate,
                userId,
                tagIds: selectedTagIds
            });
            showAlert('Успех!', `Эмоция "${selectedMood}" сохранена для ${selectedDate}`);
            if (onMoodSaved) onMoodSaved();
            setSelectedTagIds([]);
        } catch (err) {
            console.error(err);
            showAlert('Ошибка', 'Не удалось сохранить эмоцию');
        }
    };

    const handleCreateEmoji = async () => {
        if (!newEmojiName.trim()) {
            showAlert('Внимание', 'Введите название эмоции');
            return;
        }
        if (!newEmojiSymbol.trim()) {
            showAlert('Внимание', 'Выберите или введите смайлик');
            return;
        }
        try {
            await createMoodType({ name: newEmojiName, emoji: newEmojiSymbol });
            await fetchMoodTypes();
            setShowAddEmoji(false);
            setNewEmojiName("");
            setNewEmojiSymbol("😊");
            showAlert('Успех!', 'Эмоция добавлена в список');
        } catch (err) {
            console.error(err);
            showAlert('Ошибка', 'Не удалось создать эмоцию');
        }
    };

    const handleDeleteMoodType = async (id, name) => {
        showConfirm('Удаление эмоции', `Удалить эмоцию "${name}"? Все записи с этой эмоцией потеряют привязку к типу.`, async () => {
            setDeletingMoodId(id);
            try {
                await deleteMoodType(id);
                await fetchMoodTypes();
                if (selectedMood === name) {
                    const newList = moodsList.filter(m => m.id !== id);
                    if (newList.length > 0) setSelectedMood(newList[0].name);
                }
                showAlert('Успех!', 'Эмоция удалена из списка');
            } catch (err) {
                console.error(err);
                showAlert('Ошибка', 'Не удалось удалить эмоцию');
            } finally {
                setDeletingMoodId(null);
            }
        });
    };

    const quickMoods = moodsList.slice(0, 5);
    const filteredMoods = moodsList.filter(m =>
        m.name.toLowerCase().includes(moodSearch.toLowerCase())
    );
    const filteredTags = tags.filter(tag =>
        tag.name.toLowerCase().includes(tagSearch.toLowerCase())
    );

    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h2>➕ Добавить эмоцию за {selectedDate}</h2>
                {showCancel && (
                    <button onClick={onCancel} style={{ width: 'auto', background: '#EAD8CA' }}>
                        ✖️ Отмена
                    </button>
                )}
            </div>

            {/* Быстрые эмоции */}
            <div className="quick-moods">
                {quickMoods.map(mood => (
                    <div
                        key={mood.id || mood.name}
                        className={`mood-chip ${selectedMood === mood.name ? 'active' : ''}`}
                        onClick={() => setSelectedMood(mood.name)}
                    >
                        <span>{mood.emoji || "😐"}</span>
                        <span>{mood.name}</span>
                    </div>
                ))}
            </div>

            {/* Расширенный список эмоций с кнопками удаления */}
            <div className="extended-picker">
                <div className="picker-header" onClick={() => setPickerOpen(!pickerOpen)}>
                    <span>▼</span>
                    <span>все эмоции (ещё +{moodsList.length - 5})</span>
                </div>
                <div className={`picker-dropdown ${pickerOpen ? 'open' : ''}`}>
                    <input
                        type="text"
                        className="search-mood"
                        placeholder="поиск эмоции..."
                        value={moodSearch}
                        onChange={e => setMoodSearch(e.target.value)}
                    />
                    <div className="mood-list" style={{ maxHeight: '200px', overflowY: 'auto' }}>
                        {filteredMoods.map(mood => (
                            <div
                                key={mood.id || mood.name}
                                style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '6px' }}
                            >
                                <div
                                    className="mood-option"
                                    onClick={() => {
                                        setSelectedMood(mood.name);
                                        setPickerOpen(false);
                                        setMoodSearch("");
                                    }}
                                    style={{ flex: 1 }}
                                >
                                    <span>{mood.emoji || "😐"}</span>
                                    <span>{mood.name}</span>
                                </div>
                                <button
                                    onClick={() => handleDeleteMoodType(mood.id, mood.name)}
                                    disabled={deletingMoodId === mood.id}
                                    style={{ width: 'auto', background: '#EAD8CA', marginLeft: '8px', padding: '4px 8px' }}
                                    title="Удалить эмоцию навсегда"
                                >
                                    {deletingMoodId === mood.id ? '⌛' : '🗑️'}
                                </button>
                            </div>
                        ))}
                    </div>
                    {/* Форма создания новой эмоции */}
                    <div style={{ marginTop: '12px', borderTop: '1px solid #EAD8CA', paddingTop: '10px' }}>
                        {!showAddEmoji ? (
                            <button onClick={() => setShowAddEmoji(true)} style={{ width: '100%', background: '#C2A07E', color: 'white' }}>
                                ➕ Создать свою эмоцию
                            </button>
                        ) : (
                            <div style={{ background: '#FFF9F2', padding: '10px', borderRadius: '20px' }}>
                                <input
                                    type="text"
                                    placeholder="Название эмоции"
                                    value={newEmojiName}
                                    onChange={e => setNewEmojiName(e.target.value)}
                                    style={{ width: '100%', marginBottom: '8px', padding: '6px', borderRadius: '20px', border: '1px solid #EAD8CA' }}
                                />
                                <div style={{ display: 'flex', gap: '8px', alignItems: 'center', flexWrap: 'wrap' }}>
                                    <input
                                        type="text"
                                        placeholder="Смайлик"
                                        value={newEmojiSymbol}
                                        onChange={e => setNewEmojiSymbol(e.target.value)}
                                        style={{ width: '80px', textAlign: 'center', fontSize: '1.5rem', padding: '4px', borderRadius: '20px', border: '1px solid #EAD8CA' }}
                                    />
                                    <div style={{ display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
                                        {extraEmojis.map(emo => (
                                            <span key={emo} onClick={() => setNewEmojiSymbol(emo)} style={{ fontSize: '1.5rem', cursor: 'pointer', padding: '4px', background: '#F0E2D4', borderRadius: '50%', width: '36px', textAlign: 'center' }}>
                                                {emo}
                                            </span>
                                        ))}
                                    </div>
                                </div>
                                <div style={{ display: 'flex', gap: '8px', marginTop: '10px' }}>
                                    <button onClick={handleCreateEmoji} style={{ width: 'auto', background: '#C2A07E', color: 'white' }}>Сохранить</button>
                                    <button onClick={() => setShowAddEmoji(false)} style={{ width: 'auto', background: '#EAD8CA' }}>Отмена</button>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            </div>

            {/* Заметка */}
            <div className="note-field">
                <textarea
                    className="auto-textarea"
                    rows="2"
                    placeholder="Заметка к этому дню"
                    value={note}
                    onChange={handleNoteChange}
                />
            </div>

            {/* Теги для эмоции */}
            <div className="tags-section">
                <h3>🏷️ теги для этой эмоции</h3>
                <div className="quick-tags">
                    {tags.slice(0, 4).map(tag => (
                        <div
                            key={tag.id}
                            className={`tag-chip ${selectedTagIds.includes(tag.id) ? 'active' : ''}`}
                            onClick={() => {
                                if (selectedTagIds.includes(tag.id))
                                    setSelectedTagIds(selectedTagIds.filter(id => id !== tag.id));
                                else
                                    setSelectedTagIds([...selectedTagIds, tag.id]);
                            }}
                        >
                            {tag.name}
                        </div>
                    ))}
                </div>
                <div className="extended-tags">
                    <div className="tags-header" onClick={() => setTagsPickerOpen(!tagsPickerOpen)}>
                        <span>▼</span>
                        <span>все хэштеги</span>
                    </div>
                    <div className={`tags-dropdown ${tagsPickerOpen ? 'open' : ''}`}>
                        <input
                            type="text"
                            className="tag-search"
                            placeholder="поиск тега..."
                            value={tagSearch}
                            onChange={e => setTagSearch(e.target.value)}
                        />
                        <div className="all-tags-list">
                            {filteredTags.map(tag => (
                                <div
                                    key={tag.id}
                                    className="tag-option"
                                    onClick={() => {
                                        if (!selectedTagIds.includes(tag.id))
                                            setSelectedTagIds([...selectedTagIds, tag.id]);
                                    }}
                                >
                                    {tag.name}
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </div>

            <button onClick={handleSaveMood}>💾 Сохранить эмоцию</button>
        </div>
    );
}