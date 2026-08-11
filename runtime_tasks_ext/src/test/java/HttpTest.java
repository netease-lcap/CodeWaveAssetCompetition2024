import com.alibaba.fastjson2.JSONObject;
import com.netease.lib.tasks.RuntimeTasksBasicSpringEnvironmentConfiguration;
import com.netease.lib.tasks.api.TaskManagerApi;
import com.netease.lib.tasks.api.mode.TaskStructure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;


@SpringBootTest(classes = RuntimeTasksBasicSpringEnvironmentConfiguration.class)
@RunWith(SpringRunner.class)
public class HttpTest {
    @Resource
    private TaskManagerApi taskManagerApi;

    @Test
    public void testHttp() {
        taskManagerApi.registerInit((String s) -> {
            System.out.println("list");
            return s;
        }, (String s) -> {
            System.out.println("add");
            return s;
        }, (String s) -> {
            System.out.println("delete");
            return s;
        }, (String s) -> {
            System.out.println("pause");
            return s;
        }, (String s) -> {
            System.out.println("start");
            return s;
        });
        taskManagerApi.addLogic((String s) -> {
            System.out.println(s);
            return s;
        }, "test");
        taskManagerApi.addLogic((String s) -> {
            System.out.println("!!!!!");
            return s;
        }, "test2");
//        taskManagerApi.listLogic(null);
//        taskManagerApi.start("test", "adfsadfwqedq");
        String taskId1 = taskManagerApi.createTask("test", "0/10 * * * * ?", "创建任务1入参");
        String taskId2 = taskManagerApi.createTask("test", "0/15 * * * * ?", "创建任务2入参");
        String taskId3 = taskManagerApi.createTask("test2", "0/15 * * * * ?", "创建任务3入参");
//        taskManagerApi.startTask(taskId1);
//        taskManagerApi.startTask(taskId2);
        List<TaskStructure> list = taskManagerApi.listTask(null);
        System.out.println(JSONObject.toJSONString(list));
//        taskManagerApi.deleteTask(list.get(0).getTaskId());
//        list = taskManagerApi.listTask(null);
//        System.out.println(JSONObject.toJSONString(list));
    }
}
