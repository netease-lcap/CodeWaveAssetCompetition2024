# XML Base64 转换工具 (XML Base64 Converter)

## 一、背景说明
`xml-base64-converter`（`com.yourcompany.xml:xml-base64-converter:1.0.0`）是一个专用于 XML 文本与 Base64 编码之间相互转换的工具库，默认以 `UTF-8` 字符集完成编解码，便于在传输与存储场景中保持文本内容的完整性与一致性。

## 二、核心功能
这是一个纯粹的 **格式转换** 工具库，专注于 XML 文本内容与 Base64 编码之间的互相转换。

**核心功能**：
1.  **XML 转 Base64**：通过 `xmlToBase64` 将 XML 字符串编码为 Base64 字符串。
2.  **Base64 转 XML**：通过 `base64ToXml` 将 Base64 字符串解码还原为 XML 字符串。
3.  **编码处理**：编解码统一使用 `UTF-8`（`StandardCharsets.UTF_8`）。
4.  **空值直通**：入参为 `null` 时直接返回 `null`，避免额外处理。
5.  **失败抛错并记录日志**：转换失败会记录错误日志并抛出 `RuntimeException`。

## 三、配置说明
本依赖库为无配置设计，引入即可使用，无需额外的环境参数配置；不需要 `application.yml` / `application.properties` 等全局配置。

## 四、接口说明

### 1. XML 转 Base64 (xmlToBase64)
将 XML 字符串转换为 Base64 编码字符串。

**入参**

| 字段名称 | 描述 | 类型 | 说明 |
| :--- | :--- | :--- | :--- |
| xmlContent | XML 字符串内容 | String | 可选<br>为 `null` 时直接返回 `null` |

**出参**

| 字段名称 | 描述 | 类型 | 说明 |
| :--- | :--- | :--- | :--- |
| result | Base64 字符串 | String | 成功返回 Base64 编码字符串<br>入参为 `null` 时返回 `null` |

### 2. Base64 转 XML (base64ToXml)
将 Base64 编码字符串还原为 XML 字符串。

**入参**

| 字段名称 | 描述 | 类型 | 说明 |
| :--- | :--- | :--- | :--- |
| base64Content | Base64 编码字符串 | String | 可选<br>为 `null` 时直接返回 `null` |

**出参**

| 字段名称 | 描述 | 类型 | 说明 |
| :--- | :--- | :--- | :--- |
| result | XML 字符串 | String | 成功返回解码后的 XML 文本<br>入参为 `null` 时返回 `null` |

## 五、注意事项
1.  **URL/连接与传输模式**：本工具库不涉及 `URL` 格式、连接信息、目录切换、被动模式或二进制传输类型（如 `FTP.BINARY_FILE_TYPE`）等配置与行为。
2.  **字符集**：编解码固定使用 **UTF-8**（`StandardCharsets.UTF_8`）。若业务侧 XML 原始数据不是 UTF-8 编码，请先在上游完成字符集统一，否则可能出现乱码。
3.  **XML 合法性**：接口仅做字符串与 Base64 的编解码转换，不做 XML 结构/格式校验；如需校验 XML 合法性，请在调用前自行处理。
4.  **异常处理策略**：当发生转换异常时（例如 `base64Content` 不是合法 Base64），方法会记录错误日志并直接抛出 `RuntimeException`，不会返回 `null`/`false` 兜底；调用方需自行捕获并处理。
5.  **日志输出**：错误日志使用 `LCAP_EXTENSION_LOGGER` 输出；其中 `xmlToBase64` 的异常日志文案为 `"Base64 to XML conversion failed"`（与方法方向不一致），排查时请以异常栈与抛错信息为准。