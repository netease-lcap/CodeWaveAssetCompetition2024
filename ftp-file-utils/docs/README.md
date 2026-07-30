# FTP文件操作工具库 (FTP File Utils)

## 一、背景说明
这是一个 **FTP 文件传输** 工具库，基于 Apache Commons Net（`commons-net`）封装 FTP 连接与传输能力，统一使用 **Base64** 作为文件内容的数据交换标准，提供简洁的 API 处理 FTP 服务器上的文件上传与下载操作，屏蔽了底层的连接、登录、目录切换、模式切换等细节。  
当前 Maven 制品信息：`ftp-file-utils`（`1.3.0`），描述为“基于Base64的FTP文件上传工具，支持从指定FTP服务器上传/下载”。

## 二、核心功能
- **FTP 上传**：接收 Base64 编码的文件内容，解码后以二进制方式上传到指定 FTP 路径（`FTP.BINARY_FILE_TYPE`）。
- **FTP 下载**：从指定 FTP 路径下载文件，并直接返回文件内容的 Base64 编码字符串。
- **按前缀批量下载**：根据文件名前缀筛选目录下文件并批量下载，返回 `Map`（文件名 → Base64 内容）。
- **按前缀下载最新文件**：根据文件名前缀在目录中筛选并选择时间最新的文件下载，返回 `Map`（文件名 → Base64 内容）。
- **智能连接策略**：自动解析 `ftpUrl`（主机/端口/路径/用户信息），默认端口 `21`，自动切换工作目录并开启本地被动模式（Local Passive Mode）。
- **编码与传输优化**：强制使用 `UTF-8` 控制编码（`setControlEncoding("UTF-8")`）避免中文路径/文件名乱码，并统一二进制传输以适配各类文件。

## 三、配置说明
本依赖库为无状态工具库，无需在 `application.yml` 中进行全局配置。所有连接信息（地址、端口、用户、密码、路径）均由接口调用时通过 `ftpUrl` 入参动态指定。

## 四、接口说明

### 1. 上传文件 (uploadFileToFtp)
将 Base64 编码的文件上传到指定 FTP 服务器路径。

**入参**
| 字段名称 | 描述 | 类型 | 说明 |
| :--- | :--- | :--- | :--- |
| base64Content | 文件内容 | String | 文件的 Base64 编码字符串（必填）；为空直接返回 `false`；非合法 Base64 会导致解码异常并返回 `false` |
| ftpUrl | FTP地址 | String | 完整 FTP 地址（必填），用于解析主机/端口/路径/登录信息；<br>格式：`ftp://user:password@host:port/path`<br>例如：`ftp://admin:123456@192.168.1.100:21/data/upload`<br>端口缺省时默认 `21` |
| fileName | 文件名 | String | 保存到 FTP 服务器上的文件名（必填），例如：`order_2023.xml` |

**出参**
| 字段名称 | 描述 | 类型 | 说明 |
| :--- | :--- | :--- | :--- |
| result | 上传结果 | Boolean | `true` 表示上传成功；`false` 表示失败（包含参数为空、连接/登录失败、传输失败、异常等） |

### 2. 下载文件 (downloadFileFromFtp)
从指定 FTP 服务器下载文件并返回 Base64 编码内容。

**入参**
| 字段名称 | 描述 | 类型 | 说明 |
| :--- | :--- | :--- | :--- |
| ftpUrl | FTP地址 | String | 完整 FTP 地址（必填），用于解析主机/端口/路径/登录信息；<br>格式：`ftp://user:password@host:port/path`<br>例如：`ftp://admin:123456@192.168.1.100:21/data/download`<br>端口缺省时默认 `21` |
| fileName | 文件名 | String | 要下载的文件名（必填），例如：`report.pdf` |

**出参**
| 字段名称 | 描述 | 类型 | 说明 |
| :--- | :--- | :--- | :--- |
| content | 文件内容 | String | 成功返回文件的 Base64 编码字符串；失败返回 `null` |

### 3. 按前缀批量下载 (downloadFilesByPrefix)
根据文件名前缀在目标目录内筛选并批量下载文件，返回文件名到 Base64 内容的映射。

**入参**
| 字段名称 | 描述 | 类型 | 说明 |
| :--- | :--- | :--- | :--- |
| ftpUrl | FTP地址 | String | 完整 FTP 地址（必填），用于解析主机/端口/路径/登录信息；<br>格式：`ftp://user:password@host:port/path`<br>例如：`ftp://admin:123456@192.168.1.100:21/data/download` |
| filePrefix | 文件名前缀 | String | 前缀（必填），用于匹配 `fileName.startsWith(filePrefix)` |

**出参**
| 字段名称 | 描述 | 类型 | 说明 |
| :--- | :--- | :--- | :--- |
| resultMap | 批量下载结果 | Map | 成功返回 `Map<String, String>`（Key 为文件名，Value 为 Base64 内容）；<br>无匹配文件时返回空 `Map`；连接失败或发生异常返回 `null` |

### 4. 按前缀下载最新文件 (downloadLatestFileByPrefix)
根据文件名前缀筛选目录文件，并下载时间最新的一个文件，返回文件名到 Base64 内容的映射。

**入参**
| 字段名称 | 描述 | 类型 | 说明 |
| :--- | :--- | :--- | :--- |
| ftpUrl | FTP地址 | String | 完整 FTP 地址（必填），用于解析主机/端口/路径/登录信息；<br>格式：`ftp://user:password@host:port/path`<br>例如：`ftp://admin:123456@192.168.1.100:21/data/download` |
| filePrefix | 文件名前缀 | String | 前缀（必填），用于匹配 `fileName.startsWith(filePrefix)` 并筛选候选文件 |

**出参**
| 字段名称 | 描述 | 类型 | 说明 |
| :--- | :--- | :--- | :--- |
| resultMap | 最新文件下载结果 | Map | 成功返回 `Map<String, String>`（通常包含 0 或 1 个条目）；<br>未找到匹配文件时返回空 `Map`；连接失败或发生异常返回 `null` |

## 五、注意事项
1. **FTP地址格式**：必须使用 `ftp://` 协议头；推荐格式为 `ftp://user:password@host:port/path`。端口缺省时默认 `21`；`path` 用于切换工作目录。
2. **认证信息处理**：仅当 `ftpUrl` 中包含 `user:password`（即存在 `userInfo` 且包含 `:`）时才会执行登录；未提供时不会主动登录，具体是否允许访问取决于 FTP 服务端策略。
3. **编码问题**：工具库强制使用 `UTF-8` 控制编码（`setControlEncoding("UTF-8")`）以支持中文文件名与路径；若 FTP 服务器不支持 UTF-8 可能出现乱码或目录切换异常。
4. **被动模式**：默认开启本地被动模式（`enterLocalPassiveMode()`），以适应大多数防火墙与 NAT 环境。
5. **目录切换与目录不存在**：工具库会尝试 `changeWorkingDirectory(path)`；若切换失败，仅记录 `warn` 日志并继续在“当前目录/根目录”执行上传或下载，不会自动创建目录。
6. **二进制传输类型**：所有传输统一使用二进制模式（`FTP.BINARY_FILE_TYPE`），避免文本模式导致的换行/编码转换问题，适用于图片、PDF、压缩包等二进制文件。
7. **异常处理策略**：接口内部会捕获异常并记录错误日志（`LCAP_EXTENSION_LOGGER`）；上传失败返回 `false`，单文件下载失败返回 `null`，批量/最新下载在连接失败或异常时返回 `null`，不会向上抛出异常导致应用崩溃。
8. **批量与最新下载结果**：按前缀批量下载会跳过下载失败的文件（仅将成功下载的文件写入 `Map`）；按前缀下载最新文件以 `FTPFile.getTimestamp()` 的时间对比筛选，若未找到匹配文件将返回空 `Map` 并记录 `warn` 日志。