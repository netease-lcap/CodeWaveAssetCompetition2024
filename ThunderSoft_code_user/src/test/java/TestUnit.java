import com.alibaba.fastjson.JSONObject;
import com.code.entity.*;

import java.util.ArrayList;
import java.util.List;

import static com.code.CompanyService.*;
import static com.code.DepartmentService.*;
import static com.code.PositionGroupService.*;
import static com.code.PositionService.getPositionByDepartmentId;
import static com.code.PositionService.getPositionById;
import static com.code.UserService.*;

/**
 * @author: cfn
 * @date: 2024/3/14 10:48
 * @description:
 */
public class TestUnit {
	public static void main(String[] args) {
		//单位
		//List<CompanyEntity> list = getAllCompany();
	//	System.out.println("全量单位" + JSONObject.toJSONString(list));
		//2647 单位--需要展示的，列出来
		CompanyEntity companyEntityById = getCompanyEntityById("13", "189");
		System.out.println("单位详情" + JSONObject.toJSONString(companyEntityById));
		CompanyEntity parentCompanyEntity = getParentCompany("13", "1318");
		System.out.println("单位父级详情" + JSONObject.toJSONString(parentCompanyEntity));
		List<CompanyEntity> companyEntityList = getChildCompanies("13", "189");
		System.out.println("单位子级列表" + JSONObject.toJSONString(companyEntityList));
		List<CompanyTreeEntity> companyEntityLists = buildCompanyTree("13", "189",new ArrayList<>());
		System.out.println("单位子级列表" + JSONObject.toJSONString(companyEntityLists));
		//部门
		List<DepartmentEntity> lists111 = getCompanyDepartments("13", "189");
		System.out.println(JSONObject.toJSONString(lists111));
		DepartmentEntity departmentEntity = getDepartmentById("6497");
		System.out.println(JSONObject.toJSONString(departmentEntity));
		CompanyEntity departmentOwnerCompany = getDepartmentOwnerCompany("7253");
		System.out.println(JSONObject.toJSONString(departmentOwnerCompany));

		//岗位
		List<PositionEntity> lists = getPositionByDepartmentId("7253");
		System.out.println(JSONObject.toJSONString(lists));
		PositionEntity position = getPositionById("126460");
		System.out.println(JSONObject.toJSONString(position));

		//用户
		List<UserEntity> userList = getUserListByPositionId("126449");
		System.out.println(JSONObject.toJSONString(userList));
		UserEntity user = getUserById("10195");
		System.out.println(JSONObject.toJSONString(user));
		CompanyEntity companyEntity = getUserOwnerCompany("30598");
		System.out.println(JSONObject.toJSONString(companyEntity));
		DepartmentEntity departmentEntity1 = getUserOwnerDepartment("30598");
		System.out.println(JSONObject.toJSONString(departmentEntity1));
		PositionEntity positionEntity = getUserMainPosition("30598");
		System.out.println(JSONObject.toJSONString(positionEntity));

		//岗位组---待定-需要
		//todo 岗位组对子系统没有接口关联
		List<PositionGroupEntity> allPositionGroups = getAllPositionGroups();
		System.out.println(JSONObject.toJSONString(allPositionGroups));
		PositionGroupEntity positionGroup = getPositionGroup(198);
		System.out.println(JSONObject.toJSONString(positionGroup));
		List<Integer> positionGroupMembers = getPositionGroupMembers(198);
		System.out.println(JSONObject.toJSONString(positionGroupMembers));
		List<Integer> positionGroups = getUserPositionGroups(30598);
		System.out.println(JSONObject.toJSONString(positionGroups));

		// todo 企业微信登录接口
	}
}
