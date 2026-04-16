import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import DashboardPage from './pages/DashboardPage';
import DetailPage from './pages/DetailPage';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route index element={<DashboardPage />} />
          <Route path="form/:id" element={<DetailPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
