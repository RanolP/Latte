[![semver](https://img.shields.io/badge/semver-2.0.0-green.svg)](http://semver.org/spec/v2.0.0.html)
[![Travis](https://img.shields.io/travis/LatteLang/Latte.svg)](https://travis-ci.org/LatteLang/Latte)
[![License](https://img.shields.io/github/license/LatteLang/Latte.svg)](https://opensource.org/licenses/MIT)
[![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/LatteLang/Latte.svg)](https://github.com/LatteLang/Latte)
[![works badge](https://cdn.rawgit.com/nikku/works-on-my-machine/v0.2.0/badge.svg)](https://github.com/nikku/works-on-my-machine)

# Latte
Lazy, Advanced, Test-friendly, exTensible, Effective

Latte 는 프로그래밍 언어입니다. Latte 는 명확하고 가독성 높은 코드를 지향하며
JVM Bytecode, JavaScript, LLVM 등 여러 플랫폼을 대상으로 개발 하는 중입니다.
Latte 는 적은 추가 런타임 라이브러리를 지향하고, 소스 간 컴파일 후에 보더라도
그 언어를 사용한 것 같은 코드를 생성하는 것이 목표입니다.

## 철학
1. 간결하게
1. 명시적으로
1. 순수하고도
1. 코드젠에 의존적이지 않으며
1. 유닛 테스트 친화적인
1. 아름다운 코드
1. 그래도 실용성이 먼저.


## TODO
### Lexer
 * 다중 줄 주석 지원
 * char 리터럴 지원
 * 다중 줄 문자열 리터럴 지원
 * \u 이스케이프 시퀀스 지원

### Parser
 * Syntax 객체를 LALR Parsing Table로 변환하기
 * LALR Parsing Table 가지고 파싱하기

### Code Generator
 * 구현

## 라이선스
The MIT License (MIT)

Copyright (c) 2017 LatteLang

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
