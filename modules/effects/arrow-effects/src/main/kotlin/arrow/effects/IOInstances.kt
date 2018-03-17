package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.core.identity
import arrow.effects.continuations.AsyncContinuation
import arrow.instance
import arrow.typeclasses.*
import arrow.typeclasses.continuations.BindingCatchContinuation
import arrow.typeclasses.continuations.BindingContinuation
import kotlin.coroutines.experimental.CoroutineContext

@instance(IO::class)
interface IOApplicativeErrorInstance : IOApplicativeInstance, ApplicativeError<ForIO, Throwable> {
    override fun <A> attempt(fa: IOOf<A>): IO<Either<Throwable, A>> =
            fa.fix().attempt()

    override fun <A> handleErrorWith(fa: IOOf<A>, f: (Throwable) -> IOOf<A>): IO<A> =
            fa.fix().handleErrorWith(f)

    override fun <A> raiseError(e: Throwable): IO<A> =
            IO.raiseError(e)
}

@instance(IO::class)
interface IOMonadInstance : IOApplicativeInstance, Monad<ForIO> {
    override fun <A, B> flatMap(fa: IOOf<A>, f: kotlin.Function1<A, IOOf<B>>): IO<B> =
            fa.fix().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForIO, Either<A, B>>): IO<B> =
            IO.tailRecM(a, f)

    override fun <A, B> flatMapIn(fa: Kind<ForIO, A>, cc: CoroutineContext, f: (A) -> Kind<ForIO, B>): IO<B> =
            fa.fix().flatMapIn(cc, f)

    override fun <A, B> ap(fa: IOOf<A>, ff: IOOf<(A) -> B>): IO<B> =
            super<IOApplicativeInstance>.ap(fa, ff)

    override fun <A, B> map(fa: IOOf<A>, f: (A) -> B): IO<B> =
            super<IOApplicativeInstance>.map(fa, f)

    override fun <B> binding(context: CoroutineContext, c: suspend BindingContinuation<ForIO, *>.() -> B): IO<B> =
            AsyncContinuation.binding(::identity, IO.async(), context, c).fix()
}

@instance(IO::class)
interface IOMonadErrorInstance : IOApplicativeErrorInstance, IOMonadInstance, MonadError<ForIO, Throwable> {
    override fun <A, B> ap(fa: IOOf<A>, ff: IOOf<(A) -> B>): IO<B> =
            super<IOMonadInstance>.ap(fa, ff).fix()

    override fun <A, B> map(fa: IOOf<A>, f: (A) -> B): IO<B> =
            super<IOMonadInstance>.map(fa, f)

    override fun <A> pure(a: A): IO<A> =
            super<IOMonadInstance>.pure(a)

    override fun <B> bindingCatch(context: CoroutineContext, catch: (Throwable) -> Throwable, c: suspend BindingCatchContinuation<ForIO, Throwable, *>.() -> B): IO<B> =
            AsyncContinuation.binding(catch, IO.async(), context, c).fix()
}

@instance(IO::class)
interface IOMonadSuspendInstance : IOMonadErrorInstance, MonadSuspend<ForIO, Throwable> {
    override fun catch(catch: Throwable): Throwable =
            catch

    override fun <A> defer(fa: () -> IOOf<A>): IO<A> =
            IO.suspend(fa)

    override fun lazy(): IO<Unit> = IO.lazy
}

@instance(IO::class)
interface IOAsyncInstance : IOMonadSuspendInstance, Async<ForIO, Throwable> {
    override fun <A> async(fa: Proc<A>): IO<A> =
            IO.async(fa)

    override fun <A> invoke(fa: () -> A): IO<A> =
            IO.invoke(fa)

    override fun <B> binding(context: CoroutineContext, c: suspend BindingContinuation<ForIO, *>.() -> B): IO<B> =
            super<IOMonadSuspendInstance>.binding(context, c)

    override fun <B> bindingCatch(context: CoroutineContext, catch: (Throwable) -> Throwable, c: suspend BindingCatchContinuation<ForIO, Throwable, *>.() -> B): IO<B> =
            super<IOMonadSuspendInstance>.bindingCatch(context, catch, c)
}

@instance(IO::class)
interface IOEffectInstance : IOAsyncInstance, Effect<ForIO, Throwable> {
    override fun <A> runAsync(fa: Kind<ForIO, A>, cb: (Either<Throwable, A>) -> IOOf<Unit>): IO<Unit> =
            fa.fix().runAsync(cb)
}

@instance(IO::class)
interface IOMonoidInstance<A> : Monoid<Kind<ForIO, A>>, Semigroup<Kind<ForIO, A>> {

    fun SM(): Monoid<A>

    override fun combine(a: IOOf<A>, b: IOOf<A>): IO<A> =
            a.fix().flatMap { a1: A -> b.fix().map { a2: A -> SM().combine(a1, a2) } }

    override fun empty(): IO<A> = IO.pure(SM().empty())
}

@instance(IO::class)
interface IOSemigroupInstance<A> : Semigroup<Kind<ForIO, A>> {

    fun SG(): Semigroup<A>

    override fun combine(a: IOOf<A>, b: IOOf<A>): IO<A> =
            a.fix().flatMap { a1: A -> b.fix().map { a2: A -> SG().combine(a1, a2) } }
}
