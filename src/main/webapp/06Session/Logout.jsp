<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
session.removeAttribute("UserId");
session.removeAttribute("UserName");

//세션의 이 메서드를 입력하면 세션이 그냥 삭제됨
session.invalidate();

response.sendRedirect("LoginForm.jsp");
%>
