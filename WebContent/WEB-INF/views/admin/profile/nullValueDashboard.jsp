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
    <skyform:body>


        <script type="text/javascript">
            Ext.onReady(function(){
                new Ext.Viewport({
                    title: "Viewport",
                    layout:'border',
                    items: [
                        { region: "center",contentEl: 'center', split: true, border: false},
                    ]
                });
                var nullValue = echarts.init(document.getElementById('main1'));

                // 指定图表的配置项和数据
                var option = {
                    title: {
                        text: '空值数量'
                    },

                    tooltip: {},
                    legend: {
                        data:['数量']
                    },
                    xAxis: {
                        type: 'category',
                        data: []
                    },
                    yAxis: {},
                    series: [{
                        name: '数量',
                        type: 'bar',
                        data: []
                    }]
                };

                loadNullValueData(nullValue);

                // 使用刚指定的配置项和数据显示图表。
                nullValue.setOption(option);
                nullValue.on('click', function (params) {
                    loadHistoryData(main2,params.name);

                });

                var main2 = echarts.init(document.getElementById('main2'));
                var option2 = {
                    legend: {
                        data:['日平均总记录数','日平均空值记录数','比例']
                    },
                    xAxis: [
                        {
                            name: '日期',
                            type: 'category',
                            data: [],
                            axisPointer: {
                                type: 'shadow'
                            }
                        }
                    ],
                    yAxis: [
                        {
                            type: 'value',
                            name: '数量',
                            min: 0,
                            axisLabel: {
                                formatter: '{value}'
                            }
                        },
                        {
                            type: 'value',
                            name: '百分比',
                            interval: 5,
                            axisLabel: {
                                formatter: '{value}'
                            }
                        }
                    ],
                    series: [
                        {
                            name:'日平均总记录数',
                            type:'bar',
                            data:[]
                        },
                        {
                            name:'日平均空值记录数',
                            type:'bar',
                            data:[]
                        },
                        {
                            name:'比例',
                            type:'line',
                            yAxisIndex: 1,
                            data:[]
                        }
                    ]
                };

                main2.setOption(option2);
            });


            loadNullValueData = function(myChart){
                myChart.showLoading();
                Ext.Ajax.request( {
                    url : '../profileTableResult/getNullValueDashboardData.shtml',
                    success : function(response) {
                        myChart.hideLoading();
                        var obj = Ext.util.JSON.decode(response.responseText);
                        myChart.setOption({
                            xAxis: {
                                data: obj.categories
                            },
                            series: [{
                                // 根据名字对应到相应的系列
                                name: '数量',
                                data: obj.datas
                            }]
                        });
                    },
                    failure : function(response) {
                        myChart.hideLoading();
                        var obj = Ext.util.JSON.decode(response.responseText);
                        Ext.Msg.alert('提示', obj.msg);
                    },
                    params : {
                        'profileTable.profileTableId' : document.getElementById("profileTableId").value
                    }
                });

            }

            loadHistoryData = function(myChart,columnName){
                myChart.showLoading();
                Ext.Ajax.request( {
                    url : '../profileTableResult/getHistoryData.shtml',
                    success : function(response) {
                        myChart.hideLoading();
                        var obj = Ext.util.JSON.decode(response.responseText);
                        myChart.setOption({
                            xAxis: [
                                {
                                    name: '日期',
                                    type: 'category',
                                    data: obj.times,
                                    axisPointer: {
                                        type: 'shadow'
                                    }
                                }
                            ],
                            series: [
                                {
                                    name:'日平均总记录数',
                                    type:'bar',
                                    data:obj.allCounts
                                },
                                {
                                    name:'日平均空值记录数',
                                    type:'bar',
                                    data:obj.nullCounts
                                },
                                {
                                    name:'比例',
                                    type:'line',
                                    yAxisIndex: 1,
                                    data:obj.percentages
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
                        'profileTable.profileTableId' : document.getElementById("profileTableId").value,
                        'profileTableColumn.profileTableColumnName' : columnName
                    }
                });
            }

        </script>

        <div id="center">
            <input id="profileTableId" TYPE="hidden" value="${profileTableId}">
            <table style="height: 100%;width: 100%">
                <tr>
                    <td style="text-align: center; padding: 10px" >
                        <div id="main1" style="width: 450px;height:400px;"></div>
                    </td>
                    <td style="text-align: center; padding: 10px" >
                        <div id="main2" style="width: 450px;height:400px;"></div>
                    </td>
                </tr>
            </table>
        </div>

    </skyform:body>
</skyform:html>
