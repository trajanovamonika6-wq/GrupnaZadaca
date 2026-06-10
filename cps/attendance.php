<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type, Authorization");
header("Content-Type: application/json");

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

require_once 'db.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $records = json_decode(file_get_contents("php://input"), true);
    
    $stmt = $pdo->prepare("INSERT INTO attendance (student_id, student_name, timestamp, class_name) VALUES (?, ?, ?, ?)");
    
    foreach ($records as $record) {
        $stmt->execute([
            $record['studentId'],
            $record['studentName'],
            $record['timestamp'],
            $record['className']
        ]);
    }
    
    echo json_encode(["message" => "Synced successfully", "success" => true]);

} else if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $stmt = $pdo->query("SELECT * FROM attendance ORDER BY timestamp DESC");
    $records = $stmt->fetchAll(PDO::FETCH_ASSOC);
    echo json_encode($records);
}
?>