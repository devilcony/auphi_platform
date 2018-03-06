<%--
  Created by IntelliJ IDEA.
  User: Mikey
  Date: 16/7/16
  Time: 下午1:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset//UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title>excelInput</title>
</head>
<body>
<script type="text/javascript">
    var ExcelInputDialog_StartRow_Column                   ='<spring:message code="ExcelInputDialog.StartRow.Column"/> ';//起始行
    var ExcelInputDialog_PreviewSize_DialogTitle           ='<spring:message code="ExcelInputDialog.PreviewSize.DialogTitle"/> ';//输入预览数量
    var ExcelInputDialog_FilenameAdd_Button                ='<spring:message code="ExcelInputDialog.FilenameAdd.Button"/> ';//&增加
    var ExcelInputDialog_Decimal_Column                    ='<spring:message code="ExcelInputDialog.Decimal.Column"/> ';//小数
    var ExcelInputDialog_Filename_Label                    ='<spring:message code="ExcelInputDialog.Filename.Label"/> ';//文件或目录
    var ExcelInputDialog_AcceptFilenames_Label             ='<spring:message code="ExcelInputDialog.AcceptFilenames.Label"/> ';//从前面的步骤获取文件名
    var ExcelInput_Error_NotInputFieldsDefined             ='<spring:message code="ExcelInput.Error.NotInputFieldsDefined"/> ';//没定义输入字段\!
    var ExcelInputDialog_StrictTypes_Label                 ='<spring:message code="ExcelInputDialog.StrictTypes.Label"/> ';//严格类型?
    var ExcelInput_Exception_InvalidTypeBoolean            ='<spring:message code="ExcelInput.Exception.InvalidTypeBoolean"/> ';//无效的类型 Boolean, 期望的类型 {0}
    var ExcelInputMeta_CheckResult_ExpectedFilesOk         ='<spring:message code="ExcelInputMeta.CheckResult.ExpectedFilesOk"/> ';//步骤正在读取 {0} 文件.
    var ExcelInputDialog_Type_Column                       ='<spring:message code="ExcelInputDialog.Type.Column"/> ';//类型
    var ExcelInputDialog_ClearFieldList_DialogTitle        ='<spring:message code="ExcelInputDialog.ClearFieldList.DialogTitle"/> ';//问题
    var ExcelInputDialog_FilenameEdit_Button               ='<spring:message code="ExcelInputDialog.FilenameEdit.Button"/> ';//&编辑
    var ExcelInputDialog_AcceptFilenames_Tooltip           ='<spring:message code="ExcelInputDialog.AcceptFilenames.Tooltip"/> ';//如果在其他步骤中定义了文件名,\n想在本步骤中接受这些文件名,则选中该选项
    var ExcelInputDialog_ErrorIgnored_Label                ='<spring:message code="ExcelInputDialog.ErrorIgnored.Label"/> ';//忽略错误?
    var ExcelInputDialog_Dialog_SpecifyASampleFile_Message ='<spring:message code="ExcelInputDialog.Dialog.SpecifyASampleFile.Message"/> ';//请至少选择一个文件名.\r\n如果是从上一步骤来获取文件名,则选择的文件名不起作用.
    var ExcelInputDialog_TrimType_Column                   ='<spring:message code="ExcelInputDialog.TrimType.Column"/> ';//去除空格类型
    var ExcelInputDialog_FilesRead_DialogTitle             ='<spring:message code="ExcelInputDialog.FilesRead.DialogTitle"/> ';//文件读取
    var ExcelInput_Exception_InvalidTypeNumber             ='<spring:message code="ExcelInput.Exception.InvalidTypeNumber"/> ';//无效的类型 Number\: {0}, 期望的类型 {1}
    var ExcelInputDialog_Wildcard_Tooltip                  ='<spring:message code="ExcelInputDialog.Wildcard.Tooltip"/> ';//Enter a regular expression here and a directory in the first column.
    var ExcelInputMeta_TrimType_Both                       ='<spring:message code="ExcelInputMeta.TrimType.Both"/> ';//去除两边空格
    var ExcelInput_Error_NoFileSpecified                   ='<spring:message code="ExcelInput.Error.NoFileSpecified"/> ';//停止处理,没有指定文件\!
    var ExcelInput_Log_GetSheet                            ='<spring:message code="ExcelInput.Log.GetSheet"/> ';//获取表单 \#{0}
    var ExcelInputDialog_SheetsTab_TabTitle                ='<spring:message code="ExcelInputDialog.SheetsTab.TabTitle"/> ';//工作表
    var ExcelInputDialog_Filemask_Label                    ='<spring:message code="ExcelInputDialog.Filemask.Label"/> ';//正则表达式
    var ExcelInputDialog_NoFilesFound_DialogMessage        ='<spring:message code="ExcelInputDialog.NoFilesFound.DialogMessage"/> ';//找不到文件\!  Please check the filename/directory and regular expression options.
    var ExcelInputDialog_PreviewSize_DialogMessage         ='<spring:message code="ExcelInputDialog.PreviewSize.DialogMessage"/> ';//输入你想预览的记录行数量\:
    var ExcelInputMeta_CheckResult_NoInputOk               ='<spring:message code="ExcelInputMeta.CheckResult.NoInputOk"/> ';//Not receiving any input from other steps.
    var ExcelInputMeta_CheckResult_ExpectedFilesError      ='<spring:message code="ExcelInputMeta.CheckResult.ExpectedFilesError"/> ';//No files can be found to read.
    var ExcelInput_Exception_CanNotCreateFileObject        ='<spring:message code="ExcelInput.Exception.CanNotCreateFileObject"/> ';//为 {0} 创建文件对象时发生错误
    var ExcelInputDialog_ShowFiles_Button                  ='<spring:message code="ExcelInputDialog.ShowFiles.Button"/> ';//&显示文件名称...
    var ExcelInputDialog_StopOnEmpty_Label                 ='<spring:message code="ExcelInputDialog.StopOnEmpty.Label"/> ';//停在空记录
    var ExcelInputDialog_UnableToFindFields_DialogMessage  ='<spring:message code="ExcelInputDialog.UnableToFindFields.DialogMessage"/> ';//无法在Excel文件里找到任何字段.
    var ExcelInputDialog_LineNrDestDir_Label               ='<spring:message code="ExcelInputDialog.LineNrDestDir.Label"/> ';//失败的记录数文件目录
    var ExcelInputDialog_AcceptField_Tooltip               ='<spring:message code="ExcelInputDialog.AcceptField.Tooltip"/> ';//指定输入流中一个字段名,该字段中保存了需要的文件名
    var ExcelInputDialog_NoEmpty_Tooltip                   ='<spring:message code="ExcelInputDialog.NoEmpty.Tooltip"/> ';//检查这个以便从输出记录里移除空记录.
    var ExcelInputDialog_SkipErrorLines_Tooltip            ='<spring:message code="ExcelInputDialog.SkipErrorLines.Tooltip"/> ';//Skip error lines or use null-values on incompatible types?
    var ExcelInput_Exception_MissingRequiredFiles          ='<spring:message code="ExcelInput.Exception.MissingRequiredFiles"/> ';//没有找到下列文件\: {0}
    var ExcelInput_Log_FileReadByStep                      ='<spring:message code="ExcelInput.Log.FileReadByStep"/> ';//Excel 输入步骤读取的文件
    var ExcelInputDialog_StartColumn_Column                ='<spring:message code="ExcelInputDialog.StartColumn.Column"/> ';//起始列
    var ExcelInputDialog_FilenameDelete_Button             ='<spring:message code="ExcelInputDialog.FilenameDelete.Button"/> ';//&删除
    var ExcelInputDialog_SheetNameList_Label               ='<spring:message code="ExcelInputDialog.SheetNameList.Label"/> ';//要读取的工作表列表
    var ExcelInputDialog_Required_Tooltip                  ='<spring:message code="ExcelInputDialog.Required.Tooltip"/> ';//Is this file required? \nOnly used for files without wildcards.
    var ExcelInputDialog_FilesRead_DialogMessage           ='<spring:message code="ExcelInputDialog.FilesRead.DialogMessage"/> ';//文件读取\:
    var ExcelInputDialog_Repeat_Column                     ='<spring:message code="ExcelInputDialog.Repeat.Column"/> ';//重复
    var ExcelInputDialog_AcceptStep_Tooltip                ='<spring:message code="ExcelInputDialog.AcceptStep.Tooltip"/> ';//选择一个步骤,该步骤可以输出文件名
    var ExcelInputDialog_AddResult_Tooltip                 ='<spring:message code="ExcelInputDialog.AddResult.Tooltip"/> ';//如果要将文件名添加到文件名列表中,选中该选项
    var ExcelInputDialog_Repeat_Tooltip                    ='<spring:message code="ExcelInputDialog.Repeat.Tooltip"/> ';//set this field to Y if you want to repeat values when the next are empty
    var ExcelInputMeta_TrimType_None                       ='<spring:message code="ExcelInputMeta.TrimType.None"/> ';//none
    var ExcelInputDialog_Grouping_Column                   ='<spring:message code="ExcelInputDialog.Grouping.Column"/> ';//分组
    var ExcelInputDialog_Length_Column                     ='<spring:message code="ExcelInputDialog.Length.Column"/> ';//长度
    var ExcelInputDialog_ErrorTab_TabTitle                 ='<spring:message code="ExcelInputDialog.ErrorTab.TabTitle"/> ';//错误处理
    var ExcelInputDialog_Limit_Label                       ='<spring:message code="ExcelInputDialog.Limit.Label"/> ';//限制
    var ExcelInputDialog_FieldsTab_TabTitle                ='<spring:message code="ExcelInputDialog.FieldsTab.TabTitle"/> ';//字段
    var ExcelInputDialog_ErrorReadingFile_DialogMessage    ='<spring:message code="ExcelInputDialog.ErrorReadingFile.DialogMessage"/> ';//无法读取Excel文件[{0}].\n  请检查文件, 目录和表达式.
    var ExcelInput_Exception_UnsupportedType               ='<spring:message code="ExcelInput.Exception.UnsupportedType"/> ';//不支持带有值 {1} 的类型 {0}
    var ExcelInputDialog_InclSheetRownumField_Label        ='<spring:message code="ExcelInputDialog.InclSheetRownumField.Label"/> ';//表单的行号列
    var ExcelInput_Warning_MissingFiles                    ='<spring:message code="ExcelInput.Warning.MissingFiles"/> ';//警告\:没有 {0}
    var ExcelInput_Log_OpeningFile                         ='<spring:message code="ExcelInput.Log.OpeningFile"/> ';//打开文件 \#{0}
    var ExcelInputDialog_AcceptStep_Label                  ='<spring:message code="ExcelInputDialog.AcceptStep.Label"/> ';//从哪个步骤读文件名
    var ExcelInput_Exception_InvalidTypeLabel              ='<spring:message code="ExcelInput.Exception.InvalidTypeLabel"/> ';//无效的类型 Label\: {0}, 期望的类型 {1}
    var ExcelInputDialog_InclSheetnameField_Label          ='<spring:message code="ExcelInputDialog.InclSheetnameField.Label"/> ';//工作表名称字段
    var ExcelInputDialog_Required_Column                   ='<spring:message code="ExcelInputDialog.Required.Column"/> ';//要求
    var ExcelInputDialog_ErrorReadingFile2_DialogMessage   ='<spring:message code="ExcelInputDialog.ErrorReadingFile2.DialogMessage"/> ';//无法读取Excel文件[{0}].\n  请检查文件, 目录和表达式.\n{1}
    var ExcelInputDialog_FilterNames_var_ExcelFiles        ='<spring:message code="ExcelInputDialog.FilterNames.ExcelFiles"/> ';//Excel files
    var ExcelInputDialog_Encoding_Label                    ='<spring:message code="ExcelInputDialog.Encoding.Label"/> ';//编码
    var ExcelInput_Exception_RequiredFilesNotAccessible    ='<spring:message code="ExcelInput.Exception.RequiredFilesNotAccessible"/> ';//下列文件不可以访问\: {0}\r\n
    var ExcelInputDialog_InclRownumField_Label             ='<spring:message code="ExcelInputDialog.InclRownumField.Label"/> ';//行号列
    var ExcelInputDialog_FilenameAdd_Tooltip               ='<spring:message code="ExcelInputDialog.FilenameAdd.Tooltip"/> ';//增加一个条目到文件和目录列表.
    var ExcelInputDialog_FilenameVariable                  ='<spring:message code="ExcelInputDialog.FilenameVariable"/> ';//在文件名或目录插入一个变量
    var ExcelInputDialog_ContentTab_TabTitle               ='<spring:message code="ExcelInputDialog.ContentTab.TabTitle"/> ';//内容
    var KettleCellValueException_CannotConvertFieldFromCell='<spring:message code="KettleCellValueException.CannotConvertFieldFromCell"/> ';//不能转换位于表单 {0}, 行 {1}, 列 {2}的字段"{3}"\: {4}
    var ExcelInputDialog_PreviewRows_Button                ='<spring:message code="ExcelInputDialog.PreviewRows.Button"/> ';//\ &预览记录
    var ExcelInputDialog_UnableToFindFields_DialogTitle    ='<spring:message code="ExcelInputDialog.UnableToFindFields.DialogTitle"/> ';//没有成功
    var ExcelInputDialog_AcceptingGroup_Label              ='<spring:message code="ExcelInputDialog.AcceptingGroup.Label"/> ';//从前面的步骤获取文件名
    var ExcelInput_Error_ProcessRowFromvar_Excel           ='<spring:message code="ExcelInput.Error.ProcessRowFromExcel"/> ';//错误处理 Excel 文件 [{0}] 中的行 \: {1}
    var ExcelInputDialog_SheetName_Column                  ='<spring:message code="ExcelInputDialog.SheetName.Column"/> ';//工作表名称
    var ExcelInputDialog_WarningDestDir_Label              ='<spring:message code="ExcelInputDialog.WarningDestDir.Label"/> ';//告警文件目录
    var ExcelInput_Exception_InvalidTypeDate               ='<spring:message code="ExcelInput.Exception.InvalidTypeDate"/> ';//无效的类型 Date\: {0}, 期望的类型 {1}
    var ExcelInputDialog_StopOnEmpty_Tooltip               ='<spring:message code="ExcelInputDialog.StopOnEmpty.Tooltip"/> ';//当遇到一个空记录时停止处理.
    var ExcelInputDialog_Name_Column                       ='<spring:message code="ExcelInputDialog.Name.Column"/> ';//名称
    var ExcelInputDialog_ErrorIgnored_Tooltip              ='<spring:message code="ExcelInputDialog.ErrorIgnored.Tooltip"/> ';//Ignore parsing errors that occur, optionally log information about the errors.
    var ExcelInputDialog_FileDir_Column                    ='<spring:message code="ExcelInputDialog.FileDir.Column"/> ';//文件/目录
    var ExcelInputDialog_GetSheets_Button                  ='<spring:message code="ExcelInputDialog.GetSheets.Button"/> ';//&获取工作表名称...
    var ExcelInputDialog_ClearFieldList_DialogMessage      ='<spring:message code="ExcelInputDialog.ClearFieldList.DialogMessage"/> ';//在开始前,是否要清空列的列表?
    var ExcelInputDialog_Precision_Column                  ='<spring:message code="ExcelInputDialog.Precision.Column"/> ';//精度
    var ExcelInput_Log_NoMoreFiles                         ='<spring:message code="ExcelInput.Log.NoMoreFiles"/> ';//没有要处理的文件了(处理了 {0} 个文件)
    var ExcelInputDialog_Format_Column                     ='<spring:message code="ExcelInputDialog.Format.Column"/> ';//格式
    var ExcelInputDialog_AcceptField_Label                 ='<spring:message code="ExcelInputDialog.AcceptField.Label"/> ';//保存文件名的字段名
    var ExcelInputMeta_CheckResult_AcceptFilenamesOk       ='<spring:message code="ExcelInputMeta.CheckResult.AcceptFilenamesOk"/> ';//从其它步骤获取文件名.
    var ExcelInputDialog_DialogTitle                       ='<spring:message code="ExcelInputDialog.DialogTitle"/> ';//Excel输入
    var ExcelInput_Error_FilenameFieldNotFound             ='<spring:message code="ExcelInput.Error.FilenameFieldNotFound"/> ';//输入行中没有找到文件名字段 [{0}].
    var ExcelInput_Log_OutOfIndex                          ='<spring:message code="ExcelInput.Log.OutOfIndex"/> ';//索引越界\: 移到下一个表单\!
    var ExcelInput_Log_GetLine                             ='<spring:message code="ExcelInput.Log.GetLine"/> ';//从表单 \#{1} 获取行 \#{0}
    var ExcelInputDialog_Currency_Column                   ='<spring:message code="ExcelInputDialog.Currency.Column"/> ';//货币符号
    var ExcelInputDialog_SkipErrorLines_Label              ='<spring:message code="ExcelInputDialog.SkipErrorLines.Label"/> ';//跳过错误行?
    var ExcelInputDialog_FilenameDelete_Tooltip            ='<spring:message code="ExcelInputDialog.FilenameDelete.Tooltip"/> ';//从列表中删除选择的文件.
    var ExcelInputDialog_InclFilenameField_Label           ='<spring:message code="ExcelInputDialog.InclFilenameField.Label"/> ';//文件名称字段
    var ExcelInputDialog_AddResult_Label                   ='<spring:message code="ExcelInputDialog.AddResult.Label"/> ';//添加文件名
    var ExcelInputDialog_StrictTypes_Tooltip               ='<spring:message code="ExcelInputDialog.StrictTypes.Tooltip"/> ';//考虑把错误类型当作错误?
    var ExcelInputMeta_CheckResult_NoInputError            ='<spring:message code="ExcelInputMeta.CheckResult.NoInputError"/> ';//This step is not expecting nor reading any input
    var ExcelInputDialog_GetFields_Button                  ='<spring:message code="ExcelInputDialog.GetFields.Button"/> ';//&获取来自头部数据的字段...
    var ExcelInputMeta_TrimType_Left                       ='<spring:message code="ExcelInputMeta.TrimType.Left"/> ';//去除左边空格
    var ExcelInputDialog_Header_Label                      ='<spring:message code="ExcelInputDialog.Header.Label"/> ';//头部
    var ExcelInputDialog_NoEmpty_Label                     ='<spring:message code="ExcelInputDialog.NoEmpty.Label"/> ';//非空记录
    var ExcelInputDialog_Dialog_SpecifyASampleFile_Title   ='<spring:message code="ExcelInputDialog.Dialog.SpecifyASampleFile.Title"/> ';//不能预览
    var ExcelInputDialog_ErrorDestDir_Label                ='<spring:message code="ExcelInputDialog.ErrorDestDir.Label"/> ';//错误文件目录
    var ExcelInputDialog_FilenameEdit_Tooltip              ='<spring:message code="ExcelInputDialog.FilenameEdit.Tooltip"/> ';//编辑选择的文件并从列表里移除.
    var ExcelInputDialog_Wildcard_Column                   ='<spring:message code="ExcelInputDialog.Wildcard.Column"/> ';//通配符号
    var ExcelInputDialog_FilenameList_Label                ='<spring:message code="ExcelInputDialog.FilenameList.Label"/> ';//选中的文件\:
    var ExcelInputDialog_AdditionalFieldsTab_TabTitle      ='<spring:message code="ExcelInputDialog.AdditionalFieldsTab.TabTitle"/> ';//其他输出字段
    var ExcelInputDialog_ShortFileFieldName_Label          ='<spring:message code="ExcelInputDialog.ShortFileFieldName.Label"/> ';//文件名字段
    var ExcelInputDialog_PathFieldName_Label               ='<spring:message code="ExcelInputDialog.PathFieldName.Label"/> ';//路径字段
    var ExcelInputDialog_IsHiddenName_Label                ='<spring:message code="ExcelInputDialog.IsHiddenName.Label"/> ';//是否为隐藏文件字段
    var ExcelInputDialog_LastModificationTimeName_Label    ='<spring:message code="ExcelInputDialog.LastModificationTimeName.Label"/> ';//最后修改时间字段
    var ExcelInputDialog_UriName_Label                     ='<spring:message code="ExcelInputDialog.UriName.Label"/> ';//Uri 字段
    var ExcelInputDialog_RootUriName_Label                 ='<spring:message code="ExcelInputDialog.RootUriName.Label"/> ';//Root uri 字段
    var ExcelInputDialog_ExtensionFieldName_Label          ='<spring:message code="ExcelInputDialog.ExtensionFieldName.Label"/> ';//扩展名字段
    var ExcelInputDialog_SizeFieldName_Label               ='<spring:message code="ExcelInputDialog.SizeFieldName.Label"/> ';//文件大小字段
    var ExcelInputDialog_SpreadSheetType_Label             ='<spring:message code="ExcelInputDialog.SpreadSheetType.Label"/> ';//表格类型 (引擎)
    var ExcelInputDialog_ExcludeFilemask_Label             ='<spring:message code="ExcelInputDialog.ExcludeFilemask.Label"/> ';//正则表达式(排除)
    var ExcelInputDialog_AddFields                         ='<spring:message code="ExcelInputDialog.AddFields"/> ';//添加字段
    var ExcelInputDialog_Files_ExcludeWildcard_Column      ='<spring:message code="ExcelInputDialog.Files.ExcludeWildcard.Column"/> ';//通配符号(排除)
    var ExcelInputDialog_IncludeSubDirs_Column             ='<spring:message code="ExcelInputDialog.IncludeSubDirs.Column"/> "/> ';//包含子目录
</script>

</body>
</html>
