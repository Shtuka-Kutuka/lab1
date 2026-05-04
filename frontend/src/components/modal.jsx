import { useEffect } from 'react';
import ReactDOM from 'react-dom';

export default function Modal({ isOpen, onClose, title, message, onConfirm, onCancel, type = 'info' }) {
    useEffect(() => {
        if (isOpen) {
            document.body.style.overflow = 'hidden';
        } else {
            document.body.style.overflow = 'unset';
        }
        return () => {
            document.body.style.overflow = 'unset';
        };
    }, [isOpen]);

    if (!isOpen) return null;

    const handleConfirm = () => {
        if (onConfirm) onConfirm();
        onClose();
    };

    const handleCancel = () => {
        if (onCancel) onCancel();
        onClose();
    };

    return ReactDOM.createPortal(
        <div style={{
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.4)',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            zIndex: 1000,
            backdropFilter: 'blur(4px)'
        }} onClick={onClose}>
            <div style={{
                background: '#FFFCF8',
                borderRadius: '32px',
                maxWidth: '400px',
                width: '90%',
                padding: '24px',
                boxShadow: '0 20px 35px rgba(0, 0, 0, 0.1)',
                border: '1px solid #EDE0D4',
                textAlign: 'center',
                animation: 'fadeIn 0.2s ease-out'
            }} onClick={e => e.stopPropagation()}>
                <h3 style={{
                    fontSize: '1.5rem',
                    fontWeight: '500',
                    color: '#5E3A2B',
                    marginBottom: '16px',
                    borderLeft: 'none',
                    paddingLeft: 0
                }}>
                    {title}
                </h3>
                <p style={{
                    fontSize: '1rem',
                    color: '#7A563C',
                    marginBottom: '28px',
                    lineHeight: '1.4'
                }}>
                    {message}
                </p>
                <div style={{ display: 'flex', gap: '12px', justifyContent: 'center' }}>
                    {type === 'confirm' && (
                        <button onClick={handleCancel} style={{
                            background: '#EAD8CA',
                            padding: '10px 24px',
                            borderRadius: '40px',
                            border: 'none',
                            fontSize: '0.9rem',
                            fontWeight: '600',
                            color: '#4A2F21',
                            cursor: 'pointer'
                        }}>
                            Отмена
                        </button>
                    )}
                    <button onClick={handleConfirm} style={{
                        background: type === 'confirm' ? '#C2A07E' : '#DCC9B5',
                        padding: '10px 24px',
                        borderRadius: '40px',
                        border: 'none',
                        fontSize: '0.9rem',
                        fontWeight: '600',
                        color: '#4A2F21',
                        cursor: 'pointer'
                    }}>
                        {type === 'confirm' ? 'Да' : 'OK'}
                    </button>
                </div>
            </div>
        </div>,
        document.body
    );
}