<!DOCTYPE html>
<html>
<head>
    <title>Add Employee</title>
</head>
<body>
    <h1>Add Employee</h1>
    <form action="EmployeeServlet" method="post">
        <%--@declare id="id"--%><%--@declare id="name"--%><%--@declare id="department"--%><input type="hidden" name="action" value="add">
        <label for="id">ID:</label>
        <input type="number" name="id" required><br>
        <label for="name">Name:</label>
        <input type="text" name="name" required><br>
        <label for="department">Department:</label>
        <input type="text" name="department" required><br>
        <button type="submit">Add Employee</button>
    </form>
</body>
</html>
