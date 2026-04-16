import { describe, expect, it } from 'vitest';
import { mount } from '@vue/test-utils';
import BaseModal from '../BaseModal.vue';

describe('BaseModal', () => {
  it('matches structural snapshot when visible', () => {
    const wrapper = mount(BaseModal, {
      props: { title: '测试标题', visible: true },
      slots: { default: '<p class="slot-body">正文</p>' }
    });
    // 结构化快照比整段 HTML 更稳定，且不依赖 scoped 的 data-v 哈希。
    expect({
      title: wrapper.find('h3').text(),
      body: wrapper.find('.slot-body').text(),
      hasClose: wrapper.find('.close-btn').exists()
    }).toMatchSnapshot();
  });
});
