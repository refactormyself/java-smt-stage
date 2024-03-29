#include "../include/FooBar.hpp"
#include "../../Bar/include/Bar.hpp"
#include "../../Foo/include/Foo.hpp"

#include <sstream>
#include <iostream>

namespace foobar {
std::string foobarHello(int level) {
  std::stringstream buffer;

  buffer << "[" << level << "] Enter foobarHello" << std::endl;
  buffer << "[" << level << "] Exit foobarHello" << std::endl;

  return buffer.str();
}

std::string FooBar::hello(int level) {
  std::stringstream buffer;

  buffer << "[" << level << "] Enter FooBar::hello" << std::endl;
  buffer << foo::Foo::hello(level + 1);
  buffer << bar::Bar::hello(level + 1);
  buffer << "[" << level << "] Exit FooBar::hello" << std::endl;

  return buffer.str();
}

std::string FooBar::operator()() const {
  std::stringstream buffer;

  foo::Foo foo;
  bar::Bar bar;
  buffer << foo() << bar() << std::endl;

  return buffer.str();
}
}  // namespace foobar
