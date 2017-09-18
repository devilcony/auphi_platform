<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--
  Created by IntelliJ IDEA.
  User: Tony
  Date: 17/2/10
  Time: 下午5:04
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE HTML><html><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<html style="background: #E0E7F6">
<head>
    <title>详情</title>
    <style type="text/css">
        body,table{
            font-size:5px;
            font-weight:bold;
        }
        table{
            border-collapse: collapse;
            margin:0 auto;
        }
        td{
            height:30px;
        }

        .table{
            border:1px solid #235587;
            color: #1f1d20;
        }

        .table td,.table th{
            border:1px solid #235587;
        }
        .table tr.alter{
            background-color:#f5fafe;
        }
    </style>
</head>
<body>

<table width="100%" align="center"   class="table">
        <tr>
            <td>列名</td>
            <c:forEach var="stu" items="${list}">
                <td>${stu.profileTableColumn.profileTableColumnName}</td>
            </c:forEach>
        </tr>
        <tr>
            <td>类型</td>
            <c:forEach var="stu" items="${list}">
                <td>${stu.indicatorDataType}</td>
            </c:forEach>
        </tr>
    <tr>
        <td>长度</td>
        <c:forEach var="stu" items="${list}">
            <td>${stu.indicatorDataPrecision}</td>
        </c:forEach>
    </tr>
    <tr>
        <td>总数</td>
        <c:forEach var="stu" items="${list}">
            <td>${stu.indicatorAllCount}</td>
        </c:forEach>
    </tr>
    <tr>
        <td>不同值数</td>
        <c:forEach var="stu" items="${list}">
            <td>${stu.indicatorDistinctCount}</td>
        </c:forEach>
    </tr>
    <tr>
        <td>空值数</td>
        <c:forEach var="stu" items="${list}">
            <td>${stu.indicatorNullCount}</td>
        </c:forEach>
    </tr>
    <tr>
        <td>零个数</td>
        <c:forEach var="stu" items="${list}">
            <td>${stu.indicatorZeroCount}</td>
        </c:forEach>
    </tr>
    <tr>
        <td>平均值</td>
        <c:forEach var="stu" items="${list}">
            <td>${stu.indicatorAggAvg}</td>
        </c:forEach>
    </tr>
    <tr>
        <td>最大值</td>
        <c:forEach var="stu" items="${list}">
            <td>${stu.indicatorAggMax}</td>
        </c:forEach>
    </tr>
    <tr>
        <td>最小值</td>
        <c:forEach var="stu" items="${list}">
            <td>${stu.indicatorAggMin}</td>
        </c:forEach>
    </tr>
</table>
</body>
</html>
