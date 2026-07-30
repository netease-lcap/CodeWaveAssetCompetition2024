import Component from '../index';

export default {
  id: 'rich-text-dir-examples',
  title: '组件列表/RichTextDir/示例',
  component: Component,
  parameters: {
    // Optional parameter to center the component in the Canvas. More info: https://storybook.js.org/docs/configure/story-layout
    layout: 'padded',
  },
  // More on argTypes: https://storybook.js.org/docs/api/argtypes
  argTypes: {},
};

export const Example1 = {
  name: '基本用法',
  render: (args, { argTypes }) => ({
    props: Object.keys(argTypes),
    template: `
    <div><rich-text-dir :content="$props.content"></rich-text-dir>
    <br />
    <br />
    <rich-text-dir :content="$props.simpleContent"></rich-text-dir>
    </div>
      `,
  }),
  args: {
    simpleContent: `
    <h1>标题1</h1>
    <p>正文</p>
    <h2>标题2</h2>
    <p>正文</p>
    <h3>标题3</h3>
    <p>正文</p>
    <h1>标题1</h1>
    <p>正文</p>
    `,
    content: `
      <h1>人工智能在教育领域的应用</h1>
  <p>人工智能(AI)正在改变我们生活的方方面面，教育领域也不例外。从个性化学习到智能评估，AI技术正在为教育带来前所未有的变革。本文将探讨人工智能在教育中的主要应用场景、优势以及面临的挑战。</p>
  <h2>一、个性化学习系统</h2>
  <h3>1.1 自适应学习平台</h3>
  <p>自适应学习系统利用AI算法分析学生的学习数据，包括答题速度、正确率、学习时间等，然后根据这些数据为每个学生定制个性化的学习路径。例如，Knewton和Coursera等平台已经开始使用这种技术，帮助学生更高效地掌握知识。</p>
  <img src="https://picsum.photos/800/400?random=1" alt="自适应学习平台界面">
  <h3>1.2 智能辅导系统</h3>
  <p>智能辅导系统(ITS)能够模拟人类教师的辅导行为，为学生提供实时的反馈和指导。这类系统通常结合了自然语言处理(NLP)和机器学习技术，可以理解学生的问题并提供针对性的解答。例如，Carnegie Learning开发的认知导师系统，已经在数学和科学教育中取得了显著成效。</p>
  <blockquote>
    <p>"智能辅导系统不仅能够提高学生的学习成绩，还能帮助教师更有效地管理课堂时间，专注于更有价值的教学活动。"</p>
    <cite>— 教育技术专家李明</cite>
  </blockquote>
  <h2>二、智能评估与反馈</h2>
  <h3>2.1 自动评分系统</h3>
  <p>AI驱动的自动评分系统可以快速准确地评估学生的作业和考试答案，尤其是对于选择题、填空题等标准化题型。一些先进的系统甚至可以评估开放性问题，如作文和论述题，通过分析语义和结构来给出评分和改进建议。</p>
  <p>例如，Gradescope和Turnitin等平台利用AI技术实现了高效的作业和论文评分，大大减轻了教师的工作量。</p>
  <h3>2.2 学习过程分析</h3>
  <p>通过分析学生在学习平台上的交互数据，AI可以识别出学生的学习模式和潜在问题。例如，分析学生在在线测试中的答题轨迹，可以发现他们在哪些知识点上存在困难，从而及时提供额外的学习资源。</p>
  <ul>
    <li>学习时间管理分析</li>
    <li>知识掌握程度评估</li>
    <li>学习策略优化建议</li>
  </ul>
  <h2>三、虚拟学习环境</h2>
  <h3>3.1 虚拟教室与聊天机器人</h3>
  <p>虚拟教室利用AI技术创建沉浸式的学习环境，学生可以与虚拟教师和同学进行互动。聊天机器人则可以随时回答学生的问题，提供学习支持。例如，Duolingo的语言学习应用中就集成了AI聊天机器人，帮助学生练习口语和听力。</p>
  <h3>3.2 增强现实(AR)与虚拟现实(VR)教育</h3>
  <p>AR和VR技术结合AI，可以创建高度互动和个性化的学习体验。例如，在医学教育中，学生可以使用VR模拟手术操作；在历史课程中，AR可以将历史场景生动地呈现给学生。</p>
  <img src="https://picsum.photos/800/400?random=2" alt="VR教育应用">
  <h2>四、挑战与未来展望</h2>
  <h3>4.1 数据隐私与伦理问题</h3>
  <p>随着AI在教育中的应用越来越广泛，学生数据的隐私和安全问题也变得尤为重要。教育机构需要确保学生的个人信息和学习数据得到妥善保护，避免滥用和泄露。</p>
  <h3>4.2 教师角色的转变</h3>
  <p>AI技术不会完全取代教师，但会改变教师的角色。教师需要从传统的知识传授者转变为学习引导者和促进者，帮助学生发展批判性思维、创造力和解决问题的能力。</p>
  <h3>4.3 未来趋势</h3>
  <p>未来，AI在教育中的应用可能会朝着以下方向发展：</p>
  <ol>
    <li>更深入的个性化学习，实现真正的"一人一课"</li>
    <li>AI与物联网(IoT)结合，创造智能学习环境</li>
    <li>情感计算技术用于识别学生的学习情绪，提供更人性化的支持</li>
    <li>跨学科AI教育，培养学生的AI素养</li>
  </ol>
  <h2>结论</h2>
  <p>人工智能在教育领域的应用潜力巨大，它可以提高学习效率、改善教育公平性，并为教师和学生创造更丰富的学习体验。然而，要实现这些潜力，我们需要解决技术、伦理和教育实践等方面的挑战，确保AI技术能够真正服务于教育的本质目标——培养全面发展的人。</p>
      `,
  },
};
