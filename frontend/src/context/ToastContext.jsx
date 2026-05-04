import { createContext, useContext, useState } from 'react';

const ToastContext = createContext();

export const useToast = () => useContext(ToastContext);

export const ToastProvider = ({ children }) => {
    const [toast, setToast] = useState(null);
    const showToast = (message, type = 'success') => {
        setToast({ message, type });
        setTimeout(() => setToast(null), 1000);
    };
    return (
        <ToastContext.Provider value={{ showToast }}>
            {children}
            {toast && (
                <div style={{
                    position: 'fixed',
                    bottom: '24px',
                    right: '24px',
                    backgroundColor: toast.type === 'success' ? '#C2A07E' : '#DBC8B4',
                    color: '#4A2F21',
                    padding: '10px 20px',
                    borderRadius: '40px',
                    fontSize: '0.9rem',
                    fontWeight: '500',
                    zIndex: 2000,
                    boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
                    transition: 'opacity 0.2s'
                }}>
                    {toast.message}
                </div>
            )}
        </ToastContext.Provider>
    );
};