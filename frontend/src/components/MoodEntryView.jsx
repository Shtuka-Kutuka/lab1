import { useState } from 'react';
import { deleteMood } from '../api/api';
import { useModal } from '../context/ModalContext';
import { useToast } from '../context/ToastContext';

export default function MoodEntryView({ date, moods, note, onAddNew, onNoteChange, onDeleteAll, onMoodDeleted }) {
    const { showConfirm } = useModal();
    const { showToast } = useToast();
    const [isEditingNote, setIsEditingNote] = useState(false);
    const [tempNote, setTempNote] = useState(note);
    const [deletingIds, setDeletingIds] = useState([]);

    const getMoodName = (entry) => {
        return entry.mood || entry.moodType?.name || '?';
    };
    const getEmojiForMood = (moodName) => {
        const emojiMap = {
            "Радость": "😊", "Раздражение": "😤", "Тревога": "😟", "Скука": "😑",
            "Удивление": "😲", "Восторг": "🤩", "Спокойствие": "😌", "Грусть": "😔",
            "Злость": "😠", "Страх": "😨", "Интерес": "🤔", "Вина": "😣",
            "Надежда": "🙂", "Вдохновение": "✨", "Усталость": "😴", "Любовь": "🥰",
            "Стыд": "😳", "Эйфория": "🤪", "Ностальгия": "🥺", "Гордость": "😌",
            "Благодарность": "🙏"
        };
        return emojiMap[moodName] || "😐";
    };
    const getEntryEmoji = (entry) => getEmojiForMood(getMoodName(entry));

    const getAllUniqueTags = () => {
        const allTags = [];
        moods.forEach(entry => {
            if (entry.tags && entry.tags.length) {
                entry.tags.forEach(tag => {
                    if (!allTags.some(t => t.id === tag.id)) allTags.push(tag);
                });
            }
        });
        return allTags;
    };

    const handleSaveNote = () => {
        onNoteChange(tempNote);
        setIsEditingNote(false);
        showToast('Заметка сохранена', 'success');
    };
    const handleDeleteNote = () => {
        showConfirm('Удалить заметку', 'Удалить заметку без удаления эмоций?', () => {
            onNoteChange('');
            setTempNote('');
            setIsEditingNote(false);
            showToast('Заметка удалена', 'success');
        });
    };
    const handleDeleteAll = () => {
        showConfirm('Удалить день', `Удалить ВСЕ эмоции и заметку за ${date}?`, async () => {
            try {
                for (const entry of moods) if (entry.id) await deleteMood(entry.id);
                localStorage.removeItem(`note_${date}`);
                if (onDeleteAll) onDeleteAll();
                showToast('День очищен', 'success');
            } catch (err) { showToast('Ошибка удаления', 'error'); }
        });
    };
    const handleDeleteMood = (entryId, moodName) => {
        showConfirm('Удалить эмоцию', `Удалить "${moodName}" за ${date}?`, async () => {
            setDeletingIds(prev => [...prev, entryId]);
            try {
                await deleteMood(entryId);
                if (onMoodDeleted) onMoodDeleted(entryId);
                showToast(`"${moodName}" удалена`, 'success');
            } catch (err) { showToast('Ошибка удаления', 'error'); }
            finally { setDeletingIds(prev => prev.filter(id => id !== entryId)); }
        });
    };

    const uniqueTags = getAllUniqueTags();

    return (
        <div style={{ background: '#FEFAF5', borderRadius: '28px', padding: '20px', border: '1px solid #EDE0D4' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px', flexWrap: 'wrap', gap: '10px' }}>
                <h2>📖 Запись за {date}</h2>
                <button onClick={handleDeleteAll} style={{ width: 'auto', background: '#DBC8B4', color: '#5A3C2A', padding: '8px 16px' }}>🗑️ Удалить день</button>
            </div>

            <div style={{ marginBottom: '20px' }}>
                <h3>😊 Эмоции дня</h3>
                {moods.length === 0 ? <p>Нет эмоций</p> : moods.map(entry => (
                    <div key={entry.id} style={{ marginBottom: '12px', padding: '10px', background: '#FFF9F2', borderRadius: '20px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <div style={{ flex: 1 }}>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '6px' }}>
                                <span style={{ fontSize: '1.6rem' }}>{getEntryEmoji(entry)}</span>
                                <span style={{ fontWeight: '500' }}>{getMoodName(entry)}</span>
                            </div>
                            {entry.tags && entry.tags.length > 0 && (
                                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px', marginTop: '6px' }}>
                                    {entry.tags.map(tag => <span key={tag.id} style={{ background: '#F0E2D4', borderRadius: '20px', padding: '4px 12px', fontSize: '0.75rem' }}>{tag.name}</span>)}
                                </div>
                            )}
                        </div>
                        <button onClick={() => handleDeleteMood(entry.id, getMoodName(entry))} disabled={deletingIds.includes(entry.id)} style={{ background: '#EAD8CA', width: 'auto', marginLeft: '12px', padding: '6px 12px' }}>{deletingIds.includes(entry.id) ? '⌛' : '✖️'}</button>
                    </div>
                ))}
            </div>

            {uniqueTags.length > 0 && (
                <div style={{ marginBottom: '20px' }}>
                    <h3>🏷️ Все теги дня</h3>
                    <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
                        {uniqueTags.map(tag => <span key={tag.id} style={{ background: '#EADBCE', borderRadius: '20px', padding: '6px 14px', fontSize: '0.8rem' }}>{tag.name}</span>)}
                    </div>
                </div>
            )}

            <div className="note-field">
                <h3>📝 Заметка</h3>
                {isEditingNote ? (
                    <>
                        <textarea className="auto-textarea" rows="3" value={tempNote} onChange={e => setTempNote(e.target.value)} style={{ marginBottom: '10px' }} />
                        <div style={{ display: 'flex', gap: '10px' }}>
                            <button onClick={handleSaveNote} style={{ width: 'auto' }}>Сохранить</button>
                            <button onClick={() => setIsEditingNote(false)} style={{ width: 'auto', background: '#EAD8CA' }}>Отмена</button>
                            <button onClick={handleDeleteNote} style={{ width: 'auto', background: '#DBC8B4' }}>🗑️ Удалить заметку</button>
                        </div>
                    </>
                ) : (
                    <div style={{ background: '#FFF9F2', borderRadius: '20px', padding: '14px', border: '1px solid #EDE0D4' }}>
                        {note ? <p>{note}</p> : <p style={{ color: '#B68B70' }}>Нет заметки</p>}
                        <div style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
                            <button onClick={() => setIsEditingNote(true)} style={{ width: 'auto', background: '#DCC9B5' }}>✏️ Редактировать</button>
                            {note && <button onClick={handleDeleteNote} style={{ width: 'auto', background: '#DBC8B4' }}>🗑️ Удалить заметку</button>}
                        </div>
                    </div>
                )}
            </div>

            <button onClick={onAddNew} style={{ marginTop: '20px', background: '#C2A07E', color: 'white' }}>➕ Добавить эмоцию</button>
        </div>
    );
}