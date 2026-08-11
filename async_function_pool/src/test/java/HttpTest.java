import com.alibaba.fastjson2.JSONObject;
import com.netease.lib.tasks.AsyncFunctionPoolSpringEnvironmentConfiguration;
import com.netease.lib.tasks.api.FunctionManagerApi;
import com.netease.lib.tasks.model.ThreadResultDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.lang.Thread.sleep;


@SpringBootTest(classes = AsyncFunctionPoolSpringEnvironmentConfiguration.class)
@RunWith(SpringRunner.class)
public class HttpTest {
    @Resource
    private FunctionManagerApi functionManagerApi;

    @Test
    public void testAllSync() throws InterruptedException {
        Function<String, String> function1 = s -> {
            System.out.println("function1 开始执行:" + s);
            try {
                sleep(1000 * 60);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("function1 结束执行:" + s);
            return "11";
        };
        Function<String, String> function2 = s -> {
            System.out.println("function2 开始执行:" + s);
            System.out.println("function2 结束执行:" + s);
            return "22";
        };

        Function<String, String> function3 = s -> {
            System.out.println("function3 开始执行:" + s);
            try {
                sleep(1000 * 5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("function3 结束执行:" + s);
            return "33";
        };
        Function<String, String> function4 = s -> {
            System.out.println("function4 开始执行:" + s);
            try {
                sleep(1000 * 20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("function4 结束执行:" + s);
            return "44";
        };
        Map<String, Function<String, String>> functions = new HashMap<>();
        functionManagerApi.registerLogic("function1", function1);
        functionManagerApi.registerLogic("function2", function2);
        functionManagerApi.registerLogic("function3", function3);
        functionManagerApi.registerLogic("function4", function4);
        String taskId1 = functionManagerApi.asyncRunLogic("function1", "function1入参");
        String taskId2 = functionManagerApi.asyncRunLogic("function2", "function2入参");
        String taskId3 = functionManagerApi.asyncRunLogic("function3", "function3入参");
        String taskId4 = functionManagerApi.asyncRunLogic("function4", "function4入参");


        List<String> taskIdList = Arrays.asList(taskId1, taskId2, taskId3, taskId4);
        List<ThreadResultDTO> threadResultDTOS = functionManagerApi.syncGetLogicResult(taskIdList);
        System.out.println(JSONObject.toJSONString(threadResultDTOS));
//        sleep(1000 * 10);
//        threadResultDTOS = functionManagerApi.syncGetLogicResult(taskIdList);
//        System.out.println(JSONObject.toJSONString(threadResultDTOS));
//        sleep(1000 * 30);
//        threadResultDTOS = functionManagerApi.syncGetLogicResult(taskIdList);
//        System.out.println(JSONObject.toJSONString(threadResultDTOS));
//        sleep(1000 * 70);
//        threadResultDTOS = functionManagerApi.syncGetLogicResult(taskIdList);
//        System.out.println(JSONObject.toJSONString(threadResultDTOS));
    }

//    @Test
    public void testAllAsync() throws InterruptedException {
        Function<String, String> function1 = s -> {
            System.out.println("function1 开始执行:" + s);
//            try {
//                sleep(1000 * 60);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            System.out.println("function1 结束执行:" + s);
            return "11";
        };
        Function<String, String> function2 = s -> {
            System.out.println("function2 开始执行:" + s);
            System.out.println("function2 结束执行:" + s);
            return "22";
        };

        Function<String, String> function3 = s -> {
            System.out.println("function3 开始执行:" + s);
            try {
                sleep(1000 * 5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("function3 结束执行:" + s);
            return "33";
        };
        Function<String, String> function4 = s -> {
            System.out.println("function4 开始执行:" + s);
            try {
                sleep(1000 * 20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("function4 结束执行:" + s);
            return "44";
        };
        Map<String, Function<String, String>> functions = new HashMap<>();
        functionManagerApi.registerLogic("function1", function1);
        functionManagerApi.registerLogic("function2", function2);
        functionManagerApi.registerLogic("function3", function3);
        functionManagerApi.registerLogic("function4", function4);
        String taskId1 = functionManagerApi.asyncRunLogic("function1", "function1入参");
        String taskId2 = functionManagerApi.asyncRunLogic("function2", "function2入参");
        String taskId3 = functionManagerApi.asyncRunLogic("function3", "function3入参");
        String taskId4 = functionManagerApi.asyncRunLogic("function4", "function4入参");


        List<String> taskIdList = Arrays.asList(taskId1, taskId2, taskId3, taskId4);
        List<ThreadResultDTO> threadResultDTOS = functionManagerApi.asyncGetLogicResult(taskIdList);
        System.out.println(JSONObject.toJSONString(threadResultDTOS));
//        sleep(1000 * 10);
//        threadResultDTOS = functionManagerApi.syncGetLogicResult(taskIdList);
//        System.out.println(JSONObject.toJSONString(threadResultDTOS));
//        sleep(1000 * 30);
//        threadResultDTOS = functionManagerApi.syncGetLogicResult(taskIdList);
//        System.out.println(JSONObject.toJSONString(threadResultDTOS));
//        sleep(1000 * 70);
//        threadResultDTOS = functionManagerApi.syncGetLogicResult(taskIdList);
//        System.out.println(JSONObject.toJSONString(threadResultDTOS));
    }
}
