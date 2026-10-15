ShopKart E-Commerce Management System — updated package

Changes included:
- Dashboard CSS polish (stat cards, chart panels, status list, responsive spacing).
- Frontend API default changed to http://localhost:8080 to match the backend port.
- Existing CSS remains imported by frontend/src/main.jsx.

Run frontend:
  cd frontend
  npm install
  npm run dev

Run backend in a separate terminal:
  cd backend
  mvnw.cmd spring-boot:run

Configure your actual MySQL username/password in backend/src/main/resources/application.properties or via DB_USERNAME and DB_PASSWORD environment variables. Do not share database passwords.
