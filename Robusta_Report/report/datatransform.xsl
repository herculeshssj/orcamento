<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/02/xpath-functions" xmlns:xdt="http://www.w3.org/2005/02/xpath-datatypes">
<xsl:output method="text" omit-xml-declaration="yes" indent="no" />

<xsl:template match="/">
var robustaData = {
	<xsl:apply-templates select="EHSmellReport"/>
}
</xsl:template>

<xsl:template match="PackageList">
	"allBadSmellData": [
	<xsl:for-each select="Package">
		<xsl:apply-templates select="ClassList" /><xsl:if test="position()!=last()">,</xsl:if>
	</xsl:for-each>
	]
</xsl:template>

<xsl:template match="ClassList">
	<xsl:for-each select="SmellData">
		{	"packageName": 	"<xsl:value-of select="../../PackageName"/>", 
			"codeLink":	"<xsl:value-of select="LinkCode"/>", 
			"className": 	"<xsl:value-of select="ClassName"/>",
			"methodName": 	"<xsl:value-of select="MethodName"/>",
			"line":		"<xsl:value-of select="Line"/>",
			"smellType":	"<xsl:value-of select="SmellType"/>"
		}<xsl:if test="position()!=last()">,</xsl:if>
	</xsl:for-each>
</xsl:template>

<xsl:template match="/EHSmellReport">
	"projectData": {
		"name": "<xsl:value-of select="Summary/ProjectName" />",
		"date": "<xsl:value-of select="Summary/DateTime" />",
		"loc": "<xsl:value-of select="CodeInfoList/LOC" />",
		"trynumber": "<xsl:value-of select="CodeInfoList/TryNumber" />",
		"catchnumber": "<xsl:value-of select="CodeInfoList/CatchNumber" />",
		"finallynumber": "<xsl:value-of select="CodeInfoList/FinallyNumber" />"
	},
	<xsl:apply-templates select="PackageList"/>,
	<xsl:apply-templates select="AllPackageList"/>,
	<xsl:apply-templates select="EHSmellList"/>
</xsl:template>

<xsl:template match="AllPackageList">
	"allPackageData": [
		<xsl:for-each select="Package">
		[
			"<xsl:value-of select="ID" />",
			"<xsl:value-of select="PackageName" />",
			"<xsl:value-of select="LOC" />",
			"<xsl:value-of select="EmptyCatchBlock" />",
			"<xsl:value-of select="DummyHandler" />",
			"<xsl:value-of select="UnprotectedMainProgram" />",
			"<xsl:value-of select="NestedTryStatement" />",
			"<xsl:value-of select="CarelessCleanup" />",
			"<xsl:value-of select="ExceptionThrownFromFinallyBlock" />",
			"<xsl:value-of select="PackageTotal" />"	
		]<xsl:if test="position()!=last()">,</xsl:if>
		</xsl:for-each>
	]
</xsl:template>

<xsl:template match="EHSmellList">
	"badSmellTypeDensity": [
		{"type": "Empty Catch Block", "count": "<xsl:value-of select="EmptyCatchBlock" />"},
		{"type": "Dummy Handler", "count": "<xsl:value-of select="DummyHandler" />"},
		{"type": "Unprotected Main Program", "count": "<xsl:value-of select="UnprotectedMainProgram" />"},
		{"type": "Nested Try Statement", "count": "<xsl:value-of select="NestedTryStatement" />"},
		{"type": "Careless Cleanup", "count": "<xsl:value-of select="CarelessCleanup" />"},
		{"type": "Exception Thrown From Finally Block", "count": "<xsl:value-of select="ExceptionThrownFromFinallyBlock" />"}
	]
</xsl:template>

</xsl:stylesheet>