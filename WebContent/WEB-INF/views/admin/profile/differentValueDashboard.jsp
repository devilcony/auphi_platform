<%--
  Created by IntelliJ IDEA.
  User: Tony
  Date: 17/4/20
  Time: 上午10:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/common/include/taglib.jsp"%>

<skyform:html title="数据剖析-仪表盘" uxEnabled="true">
    <skyform:import src="/common/echarts.min.js"/>
    <skyform:import src="/resource/extjs3.1/Autogrid.js"/>
    <skyform:import src="/common/css/mystyle.css"/>
    <skyform:import src="/common/easyui/themes/default/easyui.css"/>
    <skyform:import src="/common/easyui/themes/default/easyui.css"/>

    <skyform:import src="/common/easyui/jquery.min.js"/>
    <skyform:import src="/common/easyui/jquery.easyui.min.js"/>
    <skyform:body>

        <script type="text/javascript">
            var v = '';

            Ext.onReady(function(){
                new Ext.Viewport({
                    title: "Viewport",
                    layout:'border',
                    items: [
                        { region: "center",contentEl: 'center', split: true, border: false},
                    ]
                });

                var main1 = echarts.init(document.getElementById('main1'));

                // 指定图表的配置项和数据
                var option = {
                    title : {
                        text: '不同值统计',
                    },
                    tooltip : {
                        trigger: 'item',
                        formatter: "{b} : {c} ({d}%)"
                    },
                    legend: {
                        orient: 'vertical',
                        left: 'left',
                        data: []
                    },
                    series : [
                        {
                            type: 'pie',
                            radius : '55%',
                            data:[
                                {value:0, name:'0'}
                            ],
                            itemStyle: {
                                emphasis: {
                                    shadowBlur: 10,
                                    shadowOffsetX: 0,
                                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                                }
                            }
                        }
                    ]
                };


                // 使用刚指定的配置项和数据显示图表。
                main1.setOption(option);
                loadDifferentValueData(main1);
                main1.on('click', function (params) {
                    v = params.name;
                    loadGridStore();
                });


               var myMask = new Ext.LoadMask("main2", {
                    msg: 'Loding...',
                    removeMask: true
                });


                loadGridStore = function(){
                    myMask.show();
                    Ext.Ajax.request( {
                        url : '../profileTableResult/getTableData.shtml',
                        success : function(response) {
                            var obj = Ext.util.JSON.decode(response.responseText);

                            $('#table').datagrid({
                                columns:[obj.columns]
                            });

                            $('#table').datagrid({
                                data: obj.datas
                            });
                            myMask.hide()
                        },
                        failure : function(response) {
                            myMask.hide()
                            var obj = Ext.util.JSON.decode(response.responseText);
                            Ext.Msg.alert('提示', obj.msg);
                        },
                        params : {
                            'profileTableColumnId' : document.getElementById("profileTableColumnId").value,
                            'value' : v=='其他' ? '' : v
                        }
                    });
                }

            });

            loadDifferentValueData = function(myChart){
                myChart.showLoading();
                Ext.Ajax.request( {
                    url : '../profileTableResult/getDifferentValueData.shtml',
                    success : function(response) {
                        myChart.hideLoading();
                        var obj = Ext.util.JSON.decode(response.responseText);
                        myChart.setOption({
                            series : [
                                {
                                    type: 'pie',
                                    radius : '55%',
                                    data:obj.datas,
                                    itemStyle: {
                                        emphasis: {
                                            shadowBlur: 10,
                                            shadowOffsetX: 0,
                                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                                        }
                                    }
                                }
                            ]
                        });
                    },
                    failure : function(response) {
                        myChart.hideLoading();
                        var obj = Ext.util.JSON.decode(response.responseText);
                        Ext.Msg.alert('提示', obj.msg);
                    },
                    params : {
                        'profileTableColumnId' : document.getElementById("profileTableColumnId").value
                    }
                });

            }


        </script>

        <div id="center">
            <input id="profileTableId" TYPE="hidden" value="${profileTableId}">
            <input id="profileTableColumnId" TYPE="hidden" value="${profileTableColumnId}">
            <table style="height: 100%;width: 100%">
                <tr>
                    <td style="text-align: center;">
                        <div id="main1" style="width: 320px;height:400px;"></div>
                    </td>
                    <td style="text-align: center; padding-top: 10px;">
                        <div id="main2" style="width: 580px;height:400px;overflow:hidden";border:1px solid #ADD8E6>
                            <table id="table" class="easyui-datagrid" style="width:100%;height:380px">

                            </table>
                        </div>
                    </td>
                </tr>
            </table>
        </div>

    </skyform:body>
</skyform:html>
