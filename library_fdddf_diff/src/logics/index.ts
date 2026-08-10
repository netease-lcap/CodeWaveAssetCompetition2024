import '@nasl/types';

import { diffLines, diffChars, diffWords } from 'diff';

export {};


/**
 * @NaslLogic
 * @type both
 * @title 行差异
 * @desc 行差异
 * @param one 第一个文本
 * @param other 第二个文本
 * @returns 返回结果 [{ value: string, added: bool, removed: bool, count: number }]
 */
export function getDiffLines(one: nasl.core.String, other: nasl.core.String): nasl.collection.List<nasl.collection.Map<nasl.core.String, nasl.core.String>> {
    if (one === other) {
        return [];
    }
    const result = diffLines(one, other);
    return convertChangedObjects(result);
}


/**
 * @NaslLogic
 * @type both
 * @title 字符差异
 * @desc 字符差异
 * @param one 第一个文本
 * @param other 第二个文本
 * @returns 返回结果 [{ value: string, added: bool, removed: bool, count: number }]
 */
export function getDiffChars(one: nasl.core.String, other: nasl.core.String): nasl.collection.List<nasl.collection.Map<nasl.core.String, nasl.core.String>> {
    if (one === other) {
        return [];
    }
    const result = diffChars(one, other);
    return convertChangedObjects(result);
}


/**
 * @NaslLogic
 * @type both
 * @title 词差异
 * @desc 词差异
 * @param one 第一个文本
 * @param other 第二个文本
 * @returns 返回结果 [{ value: string, added: bool, removed: bool, count: number }]
 */
export function getDiffWords(one: nasl.core.String, other: nasl.core.String): nasl.collection.List<nasl.collection.Map<nasl.core.String, nasl.core.String>> {
    if (one === other) {
        return [];
    }
    const result = diffWords(one, other);
    return convertChangedObjects(result);
}


function convertChangedObjects(changedObjects: any[]) {
    // Initialize the result as a list of DiffObject
    const result: nasl.collection.List<nasl.collection.Map<nasl.core.String, nasl.core.String>> = [];
  
    // Iterate through each item in the changedObjects array
    changedObjects.forEach(item => {
      // Create a DiffObject with the required fields
      const obj: nasl.collection.Map<nasl.core.String, nasl.core.String> = {
        value: item.value,
        added:  item.added ? "1" : "0",
        removed: item.removed ? "1" : "0",
        count: item.count + ""
      };
  
      // Add the object to the result list
      result.push(obj);
    });
  
    return result;
}