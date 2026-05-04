import { createContext, useContext, useState } from 'react';
import Modal from '../components/Modal';

const ModalContext = createContext();

export const useModal = () => useContext(ModalContext);

export const ModalProvider = ({ children }) => {
    const [modalState, setModalState] = useState({
        isOpen: false,
        title: '',
        message: '',
        type: 'info',
        onConfirm: null,
        onCancel: null
    });

    const showAlert = (title, message) => {
        setModalState({
            isOpen: true,
            title: title || 'Успех!',
            message,
            type: 'info',
            onConfirm: null,
            onCancel: null
        });
    };

    const showConfirm = (title, message, onConfirm, onCancel) => {
        setModalState({
            isOpen: true,
            title: title || 'Вы уверены?',
            message,
            type: 'confirm',
            onConfirm,
            onCancel: onCancel || null
        });
    };

    const closeModal = () => {
        setModalState(prev => ({ ...prev, isOpen: false }));
    };

    return (
        <ModalContext.Provider value={{ showAlert, showConfirm, closeModal }}>
            {children}
            <Modal
                isOpen={modalState.isOpen}
                onClose={closeModal}
                title={modalState.title}
                message={modalState.message}
                type={modalState.type}
                onConfirm={modalState.onConfirm}
                onCancel={modalState.onCancel}
            />
        </ModalContext.Provider>
    );
};