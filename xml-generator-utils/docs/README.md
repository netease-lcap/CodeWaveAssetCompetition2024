# 通用XML生成/解析工具库 (xml-generator-utils)

## 一、背景说明
在对接外部系统（如海关、银行、第三方支付平台）时，常需要生成/解析格式严格的 XML 报文。这些报文往往有以下要求：
1.  **字段顺序固定**：标准的 XML 解析可能不依赖顺序，但部分老旧系统或特定行业标准要求 XML 标签必须按照文档规定的顺序排列。
2.  **嵌套结构复杂**：报文通常包含多层嵌套的列表或对象。
3.  **数据源多样**：数据可能来自数据库查询结果（`Map`）、前端传递的 JSON 或其他服务的响应。

本依赖库（`xml-generator-utils`，版本 `1.5.0`）提供通用的 XML 生成/解析能力，屏蔽底层 XML 拼接与反序列化细节，让开发者聚焦数据本身与必要的顺序控制。

## 二、核心功能
**核心功能**：
1.  **JSON 转 XML**：将标准 JSON 字符串转换为 XML 格式字符串，支持自定义根节点名称（`jsonToXml`）。
2.  **Map 转 XML（可控顺序）**：支持将 `Map<String, String>` 数据转换为 XML，并允许传入 `keyOrder` 列表来**强制指定根节点下一级标签生成顺序**（`mapToXml`）。
3.  **字符串值智能展开**：在 `mapToXml` 模式下，若 `Map` 的值是 JSON 对象/数组字符串，会自动反序列化并生成嵌套 XML 结构。
4.  **XML 特殊字符转义**：生成 XML 时自动转义 `&`、`<`、`>`、`"`、`'`，避免报文破坏。
5.  **通用 XML 解析（反序列化）**：将 XML 字符串反序列化为指定类型的 Java 对象，并提供一定容错能力（`parseXml`）。

## 三、配置说明
本依赖库为无配置设计：
- 不需要 `application.yml` 全局配置；生成/解析所需信息均由入参动态指定。
- 通过 `spring.factories` 提供 Spring 环境自动装配能力，引入即可被扫描并使用。

## 四、接口说明

### 1. JSON 字符串转 XML (jsonToXml)
将一个标准的 JSON 字符串转换为 XML 格式字符串，并自动补充 `<?xml ...?>` 头。

**入参**

| 字段名称 | 描述 | 类型 | 说明 |
| :--- | :--- | :--- | :--- |
| jsonStr | 标准 JSON 格式字符串 | String | 必填。为空（`null`/空串）时返回 `null` |
| rootTagName | 根节点名称 | String | 必填。建议为合法 XML 标签名<br>示例：`Request`<br>可携带属性：`Request id="1"` |

**出参**

| 字段名称 | 描述 | 类型 | 说明 |
| :--- | :--- | :--- | :--- |
| result | XML 字符串 | String | 成功时返回 XML 字符串（包含 `<?xml version="1.0" encoding="UTF-8"?>`）<br>JSON 解析失败等异常场景会抛出 `RuntimeException` |

### 2. Map 转 XML (支持指定顺序) (mapToXml)
将 `Map<String, String>` 转换为 XML；支持通过 `keyOrder` 控制根节点直接子级标签顺序，并对值进行智能嵌套解析。

**入参**

| 字段名称 | 描述 | 类型 | 说明 |
| :--- | :--- | :--- | :--- |
| data | 数据 Map | Map&lt;String, String&gt; | 必填。为 `null` 时返回 `null`<br>Key 为标签名，Value 为文本或 JSON 字符串（对象/数组） |
| keyOrder | 顺序列表 | List&lt;String&gt; | 可选。用于控制**根节点下一级**标签输出顺序<br>为空（`null`/空列表）时回退为默认 `Map` 遍历顺序 |
| rootTagName | 根节点名称 | String | 必填。为空（`null`/空串）时返回 `null`<br>示例：`Message`<br>可携带属性：`Message type="A"` |

**出参**

| 字段名称 | 描述 | 类型 | 说明 |
| :--- | :--- | :--- | :--- |
| result | XML 字符串 | String | 成功时返回 XML 字符串（包含 `<?xml version="1.0" encoding="UTF-8"?>`）<br>入参不合法时返回 `null` |

### 3. 通用 XML 解析 (parseXml)
将 XML 字符串反序列化为指定类型的 Java 对象（忽略未知字段、属性名大小写不敏感）。

**入参**

| 字段名称 | 描述 | 类型 | 说明 |
| :--- | :--- | :--- | :--- |
| xmlContent | XML 字符串内容 | String | 必填。为 `null` 时直接抛出 `RuntimeException` |
| clazz | 目标类型 Class | Class&lt;T&gt; | 必填。用于指定反序列化目标类型（如 `MyDto.class`） |

**出参**

| 字段名称 | 描述 | 类型 | 说明 |
| :--- | :--- | :--- | :--- |
| result | 反序列化结果对象 | T | 成功时返回指定类型对象<br>解析失败会抛出 `RuntimeException`（包含错误信息） |

## 五、注意事项
1.  **根节点/标签名格式**：`rootTagName`/标签名支持携带属性（如 `Request id="1"`）。库在闭合标签时会取第一个空白分隔段作为标签名进行闭合（即闭合为 `</Request>`）。
2.  **顺序控制范围**：`keyOrder` 仅控制根节点**直接子级**的输出顺序；更深层结构的顺序取决于输入数据结构（如嵌套 `Map` 的遍历顺序）。
3.  **默认遍历顺序不保证**：当 `keyOrder` 为空时，`mapToXml` 采用 `Map` 的遍历顺序生成 XML；若使用非有序 `Map`，输出顺序可能不稳定。
4.  **字符串 JSON 智能解析策略**：`mapToXml` 会对值为字符串且形如 `{...}` / `[...]` 的内容尝试 JSON 反序列化；反序列化失败则按普通字符串写入，不会报错中断。
5.  **编码与 XML 头**：`jsonToXml` 与 `mapToXml` 生成结果均固定包含 `<?xml version="1.0" encoding="UTF-8"?>`，用于明确声明 `UTF-8` 编码。
6.  **转义处理**：生成 XML 时会自动转义 `&`、`<`、`>`、`"`、`'`，无需手动处理。
7.  **空值与缺失 Key 行为**：`mapToXml` 中 Value 为 `null` 时生成自闭合标签（如 `<Tag/>`）；`keyOrder` 中存在但 `data` 不包含的 Key 会被跳过生成。
8.  **异常处理策略**：`mapToXml` 对关键入参为空采取记录日志并返回 `null`；`jsonToXml` 在 JSON 解析失败等异常场景会抛出 `RuntimeException`；`parseXml` 对空入参与解析失败均会抛出 `RuntimeException` 并记录日志。
9.  **解析容错特性**：`parseXml` 解析时默认忽略未知属性，并对字段名大小写不敏感，适合对接字段不完全一致的 XML 报文。