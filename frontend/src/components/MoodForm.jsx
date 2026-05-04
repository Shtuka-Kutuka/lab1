import { useState, useEffect } from 'react';
import { getTags, getMoodTypes, createMoodType, deleteMoodType, saveAllMoods } from '../api/api';
import { useToast } from '../context/ToastContext';
import { useModal } from '../context/ModalContext'; // для удаления эмоции (оставляем confirm)

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
    const { showToast } = useToast();
    const { showConfirm } = useModal();
    const [availableMoods, setAvailableMoods] = useState(fallbackMoods);
    const [selectedMoods, setSelectedMoods] = useState([]);
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
            if (res.data && res.data.length) setAvailableMoods(res.data);
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

    const addMood = (mood) => {
        if (!selectedMoods.some(m => m.name === mood.name)) {
            setSelectedMoods([...selectedMoods, mood]);
        }
    };

    const removeMood = (moodName) => {
        setSelectedMoods(selectedMoods.filter(m => m.name !== moodName));
    };

    const handleSaveMoods = async () => {
        if (!selectedDate) {
            showToast('Выберите день в календаре', 'warning');
            return;
        }
        if (selectedMoods.length === 0) {
            showToast('Выберите хотя бы одну эмоцию', 'warning');
            return;
        }
        const dtos = selectedMoods.map(mood => ({
            mood: mood.name,
            date: selectedDate,
            userId,
            tagIds: selectedTagIds,
            note: note || null
        }));
        try {
            await saveAllMoods(dtos);
            showToast(`✓ Сохранено ${selectedMoods.length} эмоций`, 'success');
            setSelectedMoods([]);
            setSelectedTagIds([]);
            if (onMoodSaved) onMoodSaved();
        } catch (err) {
            console.error(err);
            showToast('Ошибка сохранения', 'error');
        }
    };

    const handleCreateEmoji = async () => {
        if (!newEmojiName.trim()) {
            showToast('Введите название эмоции', 'warning');
            return;
        }
        if (!newEmojiSymbol.trim()) {
            showToast('Выберите или введите смайлик', 'warning');
            return;
        }
        try {
            await createMoodType({ name: newEmojiName, emoji: newEmojiSymbol });
            await fetchMoodTypes();
            setShowAddEmoji(false);
            setNewEmojiName("");
            setNewEmojiSymbol("😊");
            showToast('Эмоция создана', 'success');
        } catch (err) {
            showToast('Ошибка создания', 'error');
        }
    };

    const handleDeleteMoodType = async (id, name) => {
        showConfirm('Удаление эмоции', `Удалить "${name}"? Записи потеряют привязку.`, async () => {
            setDeletingMoodId(id);
            try {
                await deleteMoodType(id);
                await fetchMoodTypes();
                if (selectedMoods.some(m => m.name === name)) {
                    setSelectedMoods(selectedMoods.filter(m => m.name !== name));
                }
                showToast('Эмоция удалена', 'success');
            } catch (err) {
                showToast('Ошибка удаления', 'error');
            } finally {
                setDeletingMoodId(null);
            }
        });
    };

    const quickMoods = availableMoods.slice(0, 5);
    const filteredMoods = availableMoods.filter(m =>
        m.name.toLowerCase().includes(moodSearch.toLowerCase())
    );
    const filteredTags = tags.filter(tag =>
        tag.name.toLowerCase().includes(tagSearch.toLowerCase())
    );

    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h2>➕ Добавить эмоции за {selectedDate}</h2>
                {showCancel && (
                    <button onClick={onCancel} style={{ width: 'auto', background: '#EAD8CA' }}>✖️ Отмена</button>
                )}
            </div>

            <div style={{ display: 'flex', gap: '24px', flexWrap: 'wrap' }}>
                {/* Левая колонка – выбор эмоций */}
                <div style={{ flex: 2, minWidth: '280px' }}>
                    <div className="quick-moods">
                        {quickMoods.map(mood => (
                            <div
                                key={mood.id || mood.name}
                                className="mood-chip"
                                onClick={() => addMood(mood)}
                            >
                                <span>{mood.emoji || "😐"}</span>
                                <span>{mood.name}</span>
                            </div>
                        ))}
                    </div>

                    <div className="extended-picker">
                        <div className="picker-header" onClick={() => setPickerOpen(!pickerOpen)}>
                            <span>▼</span>
                            <span>все эмоции (ещё +{availableMoods.length - 5})</span>
                        </div>
                        <div className={`picker-dropdown ${pickerOpen ? 'open' : ''}`}>
                            <input type="text" className="search-mood" placeholder="поиск эмоции..." value={moodSearch} onChange={e => setMoodSearch(e.target.value)} />
                            <div className="mood-list" style={{ maxHeight: '200px', overflowY: 'auto' }}>
                                {filteredMoods.map(mood => (
                                    <div key={mood.id || mood.name} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '6px' }}>
                                        <div className="mood-option" onClick={() => { addMood(mood); setPickerOpen(false); setMoodSearch(""); }} style={{ flex: 1 }}>
                                            <span>{mood.emoji || "😐"}</span>
                                            <span>{mood.name}</span>
                                        </div>
                                        <button onClick={() => handleDeleteMoodType(mood.id, mood.name)} disabled={deletingMoodId === mood.id} style={{ background: '#EAD8CA', width: 'auto', marginLeft: '8px', padding: '4px 8px' }}>🗑️</button>
                                    </div>
                                ))}
                            </div>
                            <div style={{ marginTop: '12px', borderTop: '1px solid #EAD8CA', paddingTop: '10px' }}>
                                {!showAddEmoji ? (
                                    <button onClick={() => setShowAddEmoji(true)} style={{ width: '100%', background: '#C2A07E', color: 'white' }}>➕ Создать свою эмоцию</button>
                                ) : (
                                    <div style={{ background: '#FFF9F2', padding: '10px', borderRadius: '20px' }}>
                                        <input type="text" placeholder="Название эмоции" value={newEmojiName} onChange={e => setNewEmojiName(e.target.value)} style={{ width: '100%', marginBottom: '8px', padding: '6px', borderRadius: '20px', border: '1px solid #EAD8CA' }} />
                                        <div style={{ display: 'flex', gap: '8px', alignItems: 'center', flexWrap: 'wrap' }}>
                                            <input type="text" placeholder="Смайлик" value={newEmojiSymbol} onChange={e => setNewEmojiSymbol(e.target.value)} style={{ width: '80px', textAlign: 'center', fontSize: '1.5rem', padding: '4px', borderRadius: '20px', border: '1px solid #EAD8CA' }} />
                                            <div style={{ display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
                                                {extraEmojis.map(emo => <span key={emo} onClick={() => setNewEmojiSymbol(emo)} style={{ fontSize: '1.5rem', cursor: 'pointer', padding: '4px', background: '#F0E2D4', borderRadius: '50%', width: '36px', textAlign: 'center' }}>{emo}</span>)}
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

                    <div className="note-field">
                        <textarea className="auto-textarea" rows="2" placeholder="Заметка к этому дню" value={note} onChange={handleNoteChange} />
                    </div>

                    <div className="tags-section">
                        <h3>🏷️ теги для эмоций</h3>
                        <div className="quick-tags">
                            {tags.slice(0, 4).map(tag => (
                                <div key={tag.id} className={`tag-chip ${selectedTagIds.includes(tag.id) ? 'active' : ''}`} onClick={() => {
                                    if (selectedTagIds.includes(tag.id)) setSelectedTagIds(selectedTagIds.filter(id => id !== tag.id));
                                    else setSelectedTagIds([...selectedTagIds, tag.id]);
                                }}>{tag.name}</div>
                            ))}
                        </div>
                        <div className="extended-tags">
                            <div className="tags-header" onClick={() => setTagsPickerOpen(!tagsPickerOpen)}><span>▼</span><span>все хэштеги</span></div>
                            <div className={`tags-dropdown ${tagsPickerOpen ? 'open' : ''}`}>
                                <input type="text" className="tag-search" placeholder="поиск тега..." value={tagSearch} onChange={e => setTagSearch(e.target.value)} />
                                <div className="all-tags-list">
                                    {filteredTags.map(tag => <div key={tag.id} className="tag-option" onClick={() => { if (!selectedTagIds.includes(tag.id)) setSelectedTagIds([...selectedTagIds, tag.id]); }}>{tag.name}</div>)}
                                </div>
                            </div>
                        </div>
                    </div>

                    <button onClick={handleSaveMoods}>💾 Сохранить все эмоции</button>
                </div>

                {/* Правая колонка – выбранные эмоции */}
                <div style={{ flex: 1, background: '#FEFAF5', borderRadius: '28px', padding: '16px', border: '1px solid #EDE0D4', alignSelf: 'start' }}>
                    <h3>✅ Выбранные эмоции</h3>
                    {selectedMoods.length === 0 && <p style={{ color: '#B68B70' }}>нет выбранных</p>}
                    {selectedMoods.map(mood => (
                        <div key={mood.name} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px', padding: '6px 8px', background: '#FFF9F2', borderRadius: '40px' }}>
                            <span>{mood.emoji || "😐"} {mood.name}</span>
                            <button onClick={() => removeMood(mood.name)} style={{ background: 'none', border: 'none', fontSize: '1.2rem', cursor: 'pointer' }}>✖️</button>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}