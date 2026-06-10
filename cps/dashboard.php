<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CPS Dashboard</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; background: #f5f5f5; }
        .header { background: #3f51b5; color: white; padding: 20px; text-align: center; }
        .container { max-width: 1200px; margin: 20px auto; padding: 0 20px; }
        .stats { display: flex; gap: 20px; margin-bottom: 20px; }
        .stat-card { background: white; padding: 20px; border-radius: 8px; flex: 1; text-align: center; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .stat-card h2 { margin: 0; font-size: 36px; color: #3f51b5; }
        .stat-card p { margin: 5px 0 0; color: #666; }
        .card { background: white; padding: 20px; border-radius: 8px; margin-bottom: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #eee; }
        th { background: #3f51b5; color: white; }
        tr:hover { background: #f9f9f9; }
        input { padding: 8px; margin-bottom: 10px; width: 300px; border: 1px solid #ddd; border-radius: 4px; }
    </style>
</head>
<body>
    <div class="header">
        <h1>Classroom Presence System - Dashboard</h1>
    </div>
    <div class="container">
        <div class="stats">
            <div class="stat-card">
                <h2 id="totalCount">0</h2>
                <p>Total Records</p>
            </div>
            <div class="stat-card">
                <h2 id="todayCount">0</h2>
                <p>Today's Attendance</p>
            </div>
            <div class="stat-card">
                <h2 id="studentCount">0</h2>
                <p>Unique Students</p>
            </div>
        </div>

        <div class="card">
            <h3>Attendance Chart</h3>
            <canvas id="myChart" height="100"></canvas>
        </div>

        <div class="card">
            <h3>Attendance Records</h3>
            <input type="text" id="search" placeholder="Search by name or ID..." onkeyup="filterTable()">
            <table id="attendanceTable">
                <thead>
                    <tr>
                        <th>Student ID</th>
                        <th>Student Name</th>
                        <th>Course</th>
                        <th>Timestamp</th>
                    </tr>
                </thead>
                <tbody id="tableBody"></tbody>
            </table>
        </div>
    </div>

    <script>
        let allRecords = [];
        let chart = null;

        fetch('attendance.php')
            .then(r => r.json())
            .then(data => {
                allRecords = data;
                document.getElementById('totalCount').textContent = data.length;

                const today = new Date().toISOString().slice(0, 10);
                const todayRecords = data.filter(r => r.timestamp && r.timestamp.startsWith(today));
                document.getElementById('todayCount').textContent = todayRecords.length;

                const unique = [...new Set(data.map(r => r.student_id))];
                document.getElementById('studentCount').textContent = unique.length;

                renderTable(data);
                renderChart(data);
            });

        function renderTable(data) {
            const tbody = document.getElementById('tableBody');
            tbody.innerHTML = '';
            data.forEach(r => {
                tbody.innerHTML += `<tr>
                    <td>${r.student_id}</td>
                    <td>${r.student_name}</td>
                    <td>${r.class_name}</td>
                    <td>${r.timestamp}</td>
                </tr>`;
            });
        }

        function filterTable() {
            const q = document.getElementById('search').value.toLowerCase();
            const filtered = allRecords.filter(r =>
                r.student_name.toLowerCase().includes(q) ||
                r.student_id.toLowerCase().includes(q)
            );
            renderTable(filtered);
        }

        function renderChart(data) {
            const counts = {};
            data.forEach(r => {
                const date = r.timestamp ? r.timestamp.slice(0, 10) : 'Unknown';
                counts[date] = (counts[date] || 0) + 1;
            });

            const ctx = document.getElementById('myChart').getContext('2d');
            chart = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: Object.keys(counts),
                    datasets: [{
                        label: 'Attendance per Day',
                        data: Object.values(counts),
                        backgroundColor: '#3f51b5'
                    }]
                }
            });
        }
    </script>
</body>
</html>