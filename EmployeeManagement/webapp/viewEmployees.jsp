<!DOCTYPE html>
<html>
<head>
    <title>View Employees</title>
</head>
<body>
    <h1>Employee List</h1>
    <table border="1">
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Department</th>
        </tr>
        <c:forEach var="employee" items="${employees}">
            <tr>
                <td>${employee.id}</td>
                <td>${employee.name}</td>
                <td>${employee.department}</td>
            </tr>
        </c:forEach>
    </table>
    <a href="index.jsp">Back</a>
</body>
</html>
