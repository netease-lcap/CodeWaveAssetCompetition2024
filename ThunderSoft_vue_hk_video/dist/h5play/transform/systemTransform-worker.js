importScripts('libSystemTransform.js');
    const RECORDRTP = 0;  //录制一份未经过转封装的码流原始数据，用于定位问题
    let dataType = 1;
    
    // 转封装库回调函数
    self.STCallBack = function (fileIndex,indexLen, data, dataLen)
    {
        //stFrameInfo的类型见DETAIL_FRAME_INFO
		let stFrameInfo = Module._GetDetialFrameInfo();
        let nIsMp4Index = stFrameInfo.nIsMp4Index;
		//console.log("FrameType is " , stFrameInfo);	
		//console.log("nIsMp4Index is " + nIsMp4Index);
        //debugger
        var pData = null;
        pData = new Uint8Array(dataLen);
        pData.set(Module.HEAPU8.subarray(data, data + dataLen));
        if (dataType === 1) {
            if (pData[0] == 0x49 && pData[1] == 0x4d && pData[2] == 0x4b && pData[3] == 0x48) {//码流头丢掉
                return;
            }
            postMessage({type: "outputData", buf: pData, dType: 1});
            dataType = 2;
        } else {
            if (nIsMp4Index) {
                postMessage({type: "outputData", buf: pData, dType: 6}); //6：索引类型
            } else {
                postMessage({type: "outputData", buf: pData, dType: 2}); //2:码流
            }
        }

        //stFrameInfo的类型见DETAIL_FRAME_INFO
		//let stFrameInfo = Module._GetDetialFrameInfo();
		//let stFrameType = stFrameInfo.nFrameType;
		//let nFrameNum = stFrameInfo.nFrameNum;
		//let nTimeStamp = stFrameInfo.nTimeStamp;
        //let nIsMp4Index = stFrameInfo.nIsMp4Index;
		
		//console.log("FrameType is " + stFrameType);	
		//console.log("nIsMp4Index is " + nIsMp4Index);	
        
    }

    // self.Module = { memoryInitializerRequest: loadMemInitFile(), TOTAL_MEMORY: 128*1024*1024 };
    // importScripts('SystemTransform.js');

    self.Module['onRuntimeInitialized'] = function (){
        postMessage({type: "loaded"});
    }
    onmessage = function (e) {
        var data = e.data;
        if ("create" === data.type) {
            if (RECORDRTP) {
                postMessage({type: "created"});
                postMessage({type: "outputData", buf: data.buf, dType: 1});
            } else {
                var iHeadLen = data.len;
                var pHead = Module._malloc(iHeadLen);
    
                self.writeArrayToMemory(new Uint8Array(data.buf), pHead);
                var iTransType = data.packType;//目标格式
                var iRet = Module._CreatHandle(pHead, iTransType, 4096);
                if (iRet != 0) {
                    console.log("_CreatHandle failed!" + iRet);
                } else {
                    iRet = Module._SysTransRegisterDataCallBack();			
                    if(iRet != 0)
                    {
                        console.log("_SysTransRegisterDataCallBack Failed:" + iRet);
                    }

                    iRet = Module._SysTransStart(null, null);
                    if(iRet != 0)
                    {
                        console.log("_SysTransStart Failed:" + iRet);
                    }
                    postMessage({type: "created"});
                }
            }

        } else if ("inputData" === data.type) {
            if (RECORDRTP) {
                var aFileData = new Uint8Array(data.buf);  // 拷贝一份
                var iBufferLen = aFileData.length;
                var szBufferLen = iBufferLen.toString(16);
                if (szBufferLen.length === 1) {
                    szBufferLen = "000" + szBufferLen;
                } else if (szBufferLen.length === 2) {
                    szBufferLen = "00" + szBufferLen;
                } else if (szBufferLen.length === 3) {
                    szBufferLen = "0" + szBufferLen;
                }
                var aData = [0, 0, parseInt(szBufferLen.substring(0, 2), 16), parseInt(szBufferLen.substring(2, 4), 16)];
                for(var iIndex = 0, iDataLength = aFileData.length; iIndex < iDataLength; iIndex++) {
                    aData[iIndex + 4] = aFileData[iIndex]
                }
                var dataUint8 = new Uint8Array(aData);
                postMessage({type: "outputData", buf: dataUint8.buffer, dType: 2});
            } else {
                let pInputDataBuf = Module._malloc(data.len);
                var idataLen = data.len;
                self.writeArrayToMemory(new Uint8Array(data.buf), pInputDataBuf);
                    // 输入数据，每次最多2m
                let pp = Module._SysTransInputData(0, pInputDataBuf, idataLen);
                if(pp != 0) {
                    //console.log("InputData Failed:" + pp);
                }
                Module._free(pInputDataBuf);
            }
        } else if ("release" === data.type) {
            var iRet = Module._SysTransStop();
            if (iRet != 0) {
                console.log("_SysTransStop Failed:", iRet);
            }
            Module._SysTransRelease();
            if (iRet != 0) {
                console.log("_SysTransRelease Failed:", iRet);
            }
            close();
        }
    };