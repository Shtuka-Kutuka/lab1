import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Dashboard from './pages/Dashboard';
import GoalsPage from './pages/GoalsPage';
import MoodPage from './pages/MoodPage';
import UsersPage from './pages/UsersPage';
import { ModalProvider } from './context/ModalContext';

export default function App() {
    return (
        <ModalProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Dashboard />} />
                    <Route path="/goals" element={<GoalsPage />} />
                    <Route path="/moods" element={<MoodPage />} />
                    <Route path="/users" element={<UsersPage />} />
                </Routes>
            </BrowserRouter>
        </ModalProvider>
    );
}